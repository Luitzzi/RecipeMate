import json

from llm import Reply
from pipeline import Config, convert, normalise, rule_findings, verdict_findings

PAGE = "URL: https://example.org/soup\n\n# Soup\n\n- 2 onions\n- 1 l water\n\n1. Slice the onions and boil them in the water for 20 minutes."
RECIPE = {
    "@context": "https://schema.org",
    "@type": "Recipe",
    "name": "Soup",
    "inLanguage": "en",
    "recipeIngredient": ["2 onions", "1 l water"],
    "recipeInstructions": [
        {"@type": "HowToStep", "text": "Slice the onions."},
        {"@type": "HowToStep", "text": "Boil the onions in the water for 20 minutes."},
    ],
}
INVALID = {**RECIPE, "rating": 5}
CHECKS = [
    "ingredients_complete", "instructions_complete", "nothing_invented", "values_match", "language_kept",
    "ingredient_lines", "step_focus", "steps_clear", "no_foreign_text", "grouping", "metadata", "tips_kept", "other",
]  # fmt: skip
DEFECT = 'Step "Slice the onions." has a defect.'


def verdict(failed: str | None = None, finding: str = DEFECT) -> str:
    checks = [{"id": c, "answer": "no" if c == failed else "yes", "findings": [finding] if c == failed else []} for c in CHECKS]
    return json.dumps({"checks": checks})


class FakeModel:
    """Replays scripted answers and records the calls."""

    def __init__(self, extractor: list, evaluator: list = ()):
        self.answers = {"extractor": list(extractor), "evaluator": list(evaluator)}
        self.calls = []

    def __call__(self, system_prompt, message, model, resume=None, effort=None):
        role = "evaluator" if "<recipe>" in message else "extractor"
        self.calls.append((role, message, resume))
        answer = self.answers[role].pop(0)
        return Reply(text=answer if isinstance(answer, str) else json.dumps(answer), session_id="session-1")


def config(**overrides) -> Config:
    return Config(extractor_prompt="extract", evaluator_prompt="evaluate", **overrides)


def test_good_first_answer_is_ok():
    model = FakeModel([RECIPE], [verdict()])
    result = convert(PAGE, config(), model)
    assert result.status == "ok"
    assert result.recipe == RECIPE
    assert [call[0] for call in model.calls] == ["extractor", "evaluator"]


def test_schema_failure_is_corrected_without_evaluator():
    model = FakeModel([INVALID, RECIPE], [verdict()])
    result = convert(PAGE, config(), model)
    assert result.status == "ok"
    assert [call[0] for call in model.calls] == ["extractor", "extractor", "evaluator"]
    _, feedback, resume = model.calls[1]
    assert resume == "session-1"
    assert "rating" in feedback


def test_schema_budget_exhausted_without_valid_answer_fails():
    model = FakeModel([INVALID, "no json here", INVALID])
    result = convert(PAGE, config(), model)
    assert result.status == "failed"
    assert result.recipe is None
    assert len(result.rounds) == 3


def test_evaluator_failure_is_corrected_with_findings():
    model = FakeModel([RECIPE, RECIPE], [verdict("step_focus"), verdict()])
    result = convert(PAGE, config(), model)
    assert result.status == "ok"
    _, feedback, resume = model.calls[2]
    assert resume == "session-1"
    assert f"step_focus: {DEFECT}" in feedback


def test_second_evaluator_failure_returns_needs_review():
    model = FakeModel([RECIPE, RECIPE], [verdict("step_focus"), verdict("ingredients_complete")])
    result = convert(PAGE, config(), model)
    assert result.status == "needs_review"
    assert result.recipe == RECIPE
    assert result.rounds[-1].findings == [f"ingredients_complete: {DEFECT}"]


def test_last_schema_valid_answer_is_kept_when_corrections_break_the_schema():
    model = FakeModel([RECIPE, INVALID, INVALID, INVALID], [verdict("step_focus")])
    result = convert(PAGE, config(), model)
    assert result.status == "needs_review"
    assert result.recipe == RECIPE


def test_no_retry_config_is_single_shot():
    model = FakeModel([RECIPE], [verdict("step_focus")])
    result = convert(PAGE, config(max_schema_failures=0, max_evaluator_failures=0), model)
    assert result.status == "needs_review"
    assert len(model.calls) == 2


def test_no_recipe_answer():
    model = FakeModel([{"error": "no_recipe", "reason": "Ingredients are behind a paywall."}])
    result = convert(PAGE, config(), model)
    assert result.status == "no_recipe"
    assert result.reason == "Ingredients are behind a paywall."


def test_unreadable_verdict_is_requested_again():
    model = FakeModel([RECIPE], ["I think it is fine.", verdict()])
    assert convert(PAGE, config(), model).status == "ok"


def test_non_blocking_check_is_a_note_and_passes():
    model = FakeModel([RECIPE], [verdict("metadata")])
    result = convert(PAGE, config(), model)
    assert result.status == "ok"
    assert result.rounds[0].notes == [f"metadata: {DEFECT}"]


def test_finding_without_real_quote_is_dropped():
    model = FakeModel([RECIPE], [verdict("step_focus", 'Step "Fry the leeks." is too long.')])
    assert convert(PAGE, config(), model).status == "ok"
    model = FakeModel([RECIPE], [verdict("step_focus", "Some step is too long.")])
    assert convert(PAGE, config(), model).status == "ok"


def test_evidence_may_come_from_the_page_and_be_abbreviated():
    answers = json.loads(verdict("ingredients_complete", 'The page lists "1 l water" but … "2 onions … water" is cut.'))
    findings, notes = verdict_findings(answers, RECIPE, PAGE)
    assert len(findings) == 1 and notes == []


def test_verdict_with_missing_question_is_unreadable():
    broken = json.loads(verdict())
    broken["checks"][0] = dict(broken["checks"][1])
    model = FakeModel([RECIPE], [json.dumps(broken), verdict()])
    assert convert(PAGE, config(), model).status == "ok"
    assert len(model.calls) == 3


def test_rule_findings_flag_unknown_numbers_and_images():
    recipe = {**RECIPE, "recipeIngredient": ["2 onions", "1 l water", "3 onions"], "image": ["https://example.org/x.jpg"]}
    findings, _ = rule_findings(recipe, PAGE)
    assert len(findings) == 2
    assert "3" in findings[0] and "x.jpg" in findings[1]
    assert rule_findings(RECIPE, PAGE) == ([], [])


def test_rule_findings_flag_ingredients_and_steps_not_from_the_page():
    recipe = {**RECIPE, "recipeIngredient": ["2 onions", "1 l water", "a pinch of saffron"]}
    findings, _ = rule_findings(recipe, PAGE)
    assert len(findings) == 1 and "saffron" in findings[0]

    translated = {**RECIPE, "recipeInstructions": [{"@type": "HowToStep", "text": "Die Zwiebeln schneiden und kochen."}]}
    findings, _ = rule_findings(translated, PAGE)
    assert len(findings) == 1 and "wording" in findings[0]


def test_rule_notes_for_duplicate_steps():
    step = {"@type": "HowToStep", "text": "Slice the onions."}
    findings, notes = rule_findings({**RECIPE, "recipeInstructions": [step, step]}, PAGE)
    assert findings == [] and len(notes) == 1


def test_rule_findings_flag_invented_section_names():
    steps = RECIPE["recipeInstructions"]
    invented = {**RECIPE, "recipeInstructions": [{"@type": "HowToSection", "name": "Cooking", "itemListElement": steps}]}
    findings, _ = rule_findings(invented, PAGE)
    assert len(findings) == 1 and "Cooking" in findings[0]
    from_page = {**RECIPE, "recipeInstructions": [{"@type": "HowToSection", "name": "Soup", "itemListElement": steps}]}
    assert rule_findings(from_page, PAGE) == ([], [])


def test_normalise_removes_empty_values():
    answer, notes = normalise({**RECIPE, "keywords": [], "description": None, "nutrition": {"calories": ""}}, PAGE)
    assert answer == RECIPE and len(notes) == 1


def test_normalise_dissolves_sections_with_invented_names():
    steps = RECIPE["recipeInstructions"]
    sectioned = {**RECIPE, "recipeInstructions": [{"@type": "HowToSection", "name": "Cooking", "itemListElement": steps}]}
    answer, notes = normalise(sectioned, PAGE)
    assert answer == RECIPE and "Cooking" in notes[0]
    kept = {**RECIPE, "recipeInstructions": [{"@type": "HowToSection", "name": "Soup", "itemListElement": steps}]}
    assert normalise(kept, PAGE) == (kept, [])


def test_normalise_leaves_other_answers_alone():
    assert normalise(None, PAGE) == (None, [])
    assert normalise({"error": "no_recipe", "reason": "x"}, PAGE) == ({"error": "no_recipe", "reason": "x"}, [])
