"""Converts a recipe page to schema.org Recipe JSON: extract, validate, evaluate, correct.

The extractor is expected to deliver a good result in one shot. Correction rounds are the fallback: a schema-invalid
answer goes straight back with the validation errors; a schema-valid answer is judged by the evaluator and goes back
with its findings if it is not good. Corrections continue the extractor's session, so page, prompt and previous
answer are served from the prompt cache.

Usage: uv run python pipeline.py [--all | slug ...] [--no-retry] [--no-eval] [--force] [--jobs N]
"""

import argparse
import hashlib
import json
import re
import sys
import unicodedata
from concurrent.futures import ThreadPoolExecutor
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Callable

import llm
from extract_json import extract_last_json
from reduce import reduce_dump
from textmatch import line_recall, tokens
from validation import schema_errors

ROOT = Path(__file__).resolve().parent
PROMPTS = ROOT / "prompts"
DUMPS = ROOT / "dumps"
OUT = ROOT / "out"


def load_prompt(name: str) -> str:
    text = (PROMPTS / f"{name}.md").read_text(encoding="utf-8")
    return text.replace("{{steps}}", (PROMPTS / "steps.md").read_text(encoding="utf-8").strip())


# Evaluator questions whose "no" sends the recipe back for correction. The others are recorded as notes.
BLOCKING_CHECKS = {
    "ingredients_complete", "instructions_complete", "nothing_invented", "values_match", "language_kept",
    "ingredient_lines", "step_focus", "steps_clear", "no_foreign_text",
}  # fmt: skip
NOTE_CHECKS = {"grouping", "metadata", "tips_kept", "other"}

MIN_INGREDIENT_MATCH = 0.7
MIN_STEP_WORDS_ON_PAGE = 0.6
MIN_STEP_WORDS = 4  # shorter steps give no reliable share
LONG_STEP_CHARS = 500


@dataclass
class Config:
    extractor_model: str = "haiku"
    evaluator_model: str = "haiku"
    # CLI effort level per role (low, medium, high, ...); None keeps the CLI default.
    extractor_effort: str | None = None
    evaluator_effort: str | None = None
    # Number of failures that are answered with a correction round. One more failure ends the conversion.
    max_schema_failures: int = 2
    max_evaluator_failures: int = 1
    evaluate: bool = True
    extractor_prompt: str = field(default_factory=lambda: load_prompt("extractor"))
    evaluator_prompt: str = field(default_factory=lambda: load_prompt("evaluator"))
    recipe_schema: str = "recipe"


@dataclass
class Round:
    raw: str
    schema_errors: list[str] = field(default_factory=list)
    # Blocking defects: sent back to the extractor.
    findings: list[str] = field(default_factory=list)
    # Non-blocking remarks: kept for the reader of the result.
    notes: list[str] = field(default_factory=list)
    verdict: dict | None = None
    evaluator_raw: str | None = None


@dataclass
class Result:
    # ok: schema-valid and judged good. needs_review: schema-valid, but not judged good within the budget.
    # no_recipe: the page holds no complete recipe. failed: no schema-valid answer.
    status: str
    recipe: dict | None = None
    reason: str | None = None
    rounds: list[Round] = field(default_factory=list)
    usage: list[dict] = field(default_factory=list)


def _numbers(text: str) -> set[str]:
    text = unicodedata.normalize("NFKC", text).replace("⁄", "/")
    return set(re.findall(r"\d+", text))


def _steps(recipe: dict) -> list[dict]:
    steps = []
    for item in recipe["recipeInstructions"]:
        steps += item["itemListElement"] if item["@type"] == "HowToSection" else [item]
    return steps


def _is_heading(name: str, page_lines: list[set[str]]) -> bool:
    """A heading is a short line of its own: the name's words and at most two more."""
    wanted = set(tokens(name))
    return any(wanted <= line and len(line) <= len(wanted) + 2 for line in page_lines)


def normalise(answer, markdown: str) -> tuple[object, list[str]]:
    """Repairs two harmless slips without a model round. Returns the answer and notes on what was changed.

    - Properties without a value (null, "", [], {}) are removed.
    - If a section carries a name that is not a heading of the page, the sections are dissolved into plain steps.
    """
    notes = []

    def prune(node):
        if isinstance(node, dict):
            pruned = {key: prune(value) for key, value in node.items()}
            return {key: value for key, value in pruned.items() if value not in (None, "", [], {})}
        if isinstance(node, list):
            return [value for value in (prune(item) for item in node) if value not in (None, "", [], {})]
        return node

    pruned = prune(answer)
    if pruned != answer:
        notes.append("Removed properties without a value.")
    instructions = pruned.get("recipeInstructions") if isinstance(pruned, dict) else None
    if isinstance(instructions, list):
        page_lines = [set(tokens(line)) for line in markdown.splitlines() if line.strip()]
        sections = [i for i in instructions if isinstance(i, dict) and i.get("@type") == "HowToSection"]
        invented = [
            s.get("name") for s in sections if not isinstance(s.get("name"), str) or not _is_heading(s["name"], page_lines)
        ]
        if invented and all(isinstance(s.get("itemListElement"), list) for s in sections):
            pruned["recipeInstructions"] = [
                step for item in instructions for step in (item["itemListElement"] if item in sections else [item])
            ]
            notes.append(f"Dissolved sections because these names are not headings of the page: {invented}.")
    return pruned, notes


def rule_findings(recipe: dict, markdown: str) -> tuple[list[str], list[str]]:
    """Checks that code does more reliably than a judge. Returns blocking findings and non-blocking notes."""
    findings, notes = [], []
    ingredients = recipe["recipeIngredient"]
    steps = [step["text"] for step in _steps(recipe)]

    page_numbers = _numbers(markdown)
    flagged = set()
    for kind, line in [("Ingredient", i) for i in ingredients] + [("Step", s) for s in steps]:
        unknown = sorted(_numbers(line) - page_numbers)
        if unknown:
            flagged.add(line)
            findings.append(f'{kind} "{line}" holds numbers that are not on the page: {", ".join(unknown)}.')

    page_lines = [set(tokens(line)) for line in markdown.splitlines() if line.strip()]
    for line in ingredients:
        if line not in flagged and line_recall(line, page_lines, window=4) < MIN_INGREDIENT_MATCH:
            findings.append(f'Ingredient "{line}" is not on the page in this form. Copy the line from the page.')

    page_words = set(tokens(markdown))
    for step in steps:
        words = [word for word in tokens(step) if len(word) > 2 and not word.isdigit()]
        on_page = sum(word in page_words for word in words)
        if step not in flagged and len(words) >= MIN_STEP_WORDS and on_page / len(words) < MIN_STEP_WORDS_ON_PAGE:
            findings.append(f'Step "{step}" is not in the words of the page. Keep the page\'s language and wording.')

    for item in recipe["recipeInstructions"]:
        if item["@type"] == "HowToSection" and not _is_heading(item["name"], page_lines):
            findings.append(
                f'Section name "{item["name"]}" is not a heading of the page. Use the page\'s own heading, or no '
                "sections at all if the page's instructions have none."
            )

    page = markdown.replace("\\", "")
    for url in list(recipe.get("image", [])) + [step["image"] for step in _steps(recipe) if "image" in step]:
        if url not in page:
            findings.append(f'Image URL "{url}" is not on the page.')

    for step in sorted({s for s in steps if steps.count(s) > 1}):
        notes.append(f'Step "{step}" occurs {steps.count(step)} times.')
    for step in steps:
        if len(step) > LONG_STEP_CHARS:
            notes.append(f'Step "{step[:80]}…" is {len(step)} characters long.')
    return findings, notes


def _evaluate(markdown: str, recipe: dict, config: Config, complete: Callable, usage: list[dict]) -> tuple[dict | None, str]:
    message = f"<page>\n{markdown}\n</page>\n\n<recipe>\n{json.dumps(recipe, ensure_ascii=False, indent=2)}\n</recipe>"
    raw = ""
    for _ in range(2):  # an unreadable verdict is asked for once more
        reply = complete(config.evaluator_prompt, message, config.evaluator_model, effort=config.evaluator_effort)
        usage.append({"call": "evaluator", **reply.usage})
        raw = reply.text
        verdict = extract_last_json(raw)
        if not schema_errors(verdict, "verdict") and {c["id"] for c in verdict["checks"]} == BLOCKING_CHECKS | NOTE_CHECKS:
            return verdict, raw
    return None, raw


def _squash(text: str) -> str:
    return re.sub(r"\s+", " ", text.replace("\\", "")).strip().lower()


def _has_evidence(finding: str, sources: str) -> bool:
    """A finding counts only if it quotes text that really stands in the object or on the page."""
    for quote in re.findall(r'"([^"]+)"|“([^”]+)”|«([^»]+)»|`([^`]+)`', finding):
        for part in re.split(r"\.\.\.|…", next(q for q in quote if q)):
            part = _squash(part).strip(" .,;:")
            if len(part) >= 4 and part in sources:
                return True
    return False


def verdict_findings(verdict: dict, recipe: dict, markdown: str) -> tuple[list[str], list[str]]:
    """Turns the evaluator's answers into blocking findings and notes. Findings without a real quote are dropped."""
    sources = _squash(markdown) + " " + _squash(json.dumps(recipe, ensure_ascii=False).replace('\\"', '"'))
    findings, notes = [], []
    for check in verdict["checks"]:
        if check["answer"] == "yes":
            continue
        kept = [f"{check['id']}: {finding}" for finding in check["findings"] if _has_evidence(finding, sources)]
        (findings if check["id"] in BLOCKING_CHECKS else notes).extend(kept)
    return findings, notes


SCHEMA_FEEDBACK = """Your answer was checked by a program and is not usable:

{errors}

Reply with the complete corrected JSON object only, without a <steps> block. Change nothing that is not affected \
by these errors."""

REVIEW_FEEDBACK = """Your answer was audited against the page. These defects were found:

{findings}

Reply with the complete corrected JSON object only, without a <steps> block. Correct every defect listed. Do not \
create new ones: after dividing or rewording a step, check that each resulting step still says what it acts on \
and still holds every amount, time and tip. Do not add or rename sections. Change nothing else."""


def convert(markdown: str, config: Config | None = None, complete: Callable = llm.complete) -> Result:
    config = config or Config()
    result = Result(status="failed")
    schema_failures = evaluator_failures = 0
    last_valid: dict | None = None

    def extract(message: str, resume: str | None = None) -> llm.Reply:
        return complete(
            config.extractor_prompt, message, config.extractor_model, resume=resume, effort=config.extractor_effort
        )

    reply = extract(f"<page>\n{markdown}\n</page>")
    while True:
        result.usage.append({"call": "extractor", **reply.usage})
        current = Round(raw=reply.text)
        result.rounds.append(current)
        answer, repairs = normalise(extract_last_json(reply.text), markdown)

        if answer is None:
            current.schema_errors = ["The answer holds no complete JSON object."]
        elif not schema_errors(answer, "no_recipe"):
            return Result(status="no_recipe", reason=answer["reason"], rounds=result.rounds, usage=result.usage)
        else:
            current.schema_errors = schema_errors(answer, config.recipe_schema)

        if current.schema_errors:
            schema_failures += 1
            if schema_failures > config.max_schema_failures:
                break
            feedback = SCHEMA_FEEDBACK.format(errors="\n".join(f"- {e}" for e in current.schema_errors))
        else:
            last_valid = answer
            current.findings, current.notes = rule_findings(answer, markdown)
            current.notes = repairs + current.notes
            if config.evaluate:
                current.verdict, current.evaluator_raw = _evaluate(markdown, answer, config, complete, result.usage)
                if current.verdict is None:
                    raise llm.LlmError("evaluator returned no readable verdict")
                findings, notes = verdict_findings(current.verdict, answer, markdown)
                current.findings += findings
                current.notes += notes
            if not current.findings:
                result.status, result.recipe = "ok", answer
                return result
            evaluator_failures += 1
            if evaluator_failures > config.max_evaluator_failures:
                break
            feedback = REVIEW_FEEDBACK.format(findings="\n".join(f"- {f}" for f in current.findings))

        reply = extract(feedback, resume=reply.session_id)

    if last_valid is not None:
        result.status, result.recipe = "needs_review", last_valid
    return result


def _prompt_hash(config: Config) -> str:
    parts = [config.extractor_prompt, config.evaluator_prompt if config.evaluate else "", config.extractor_model]
    parts += [config.evaluator_model, str(config.max_schema_failures), str(config.max_evaluator_failures)]
    parts += [str(config.extractor_effort), str(config.evaluator_effort), config.recipe_schema]
    return hashlib.sha256("\0".join(parts).encode()).hexdigest()[:16]


def convert_dump(slug: str, config: Config, out: Path = OUT, force: bool = False) -> dict:
    target = out / slug
    result_file = target / "result.json"
    prompt_hash = _prompt_hash(config)
    if not force and result_file.exists():
        cached = json.loads(result_file.read_text(encoding="utf-8"))
        if cached.get("prompt_hash") == prompt_hash:
            return cached

    markdown = reduce_dump(DUMPS / slug)
    target.mkdir(parents=True, exist_ok=True)
    (target / "page.md").write_text(markdown, encoding="utf-8")
    try:
        result = asdict(convert(markdown, config))
    except llm.LlmError as error:
        result = asdict(Result(status="error", reason=str(error)))
    result = {"slug": slug, "prompt_hash": prompt_hash, **result}

    recipe_file = target / "recipe.json"
    if result["recipe"] is not None:
        recipe_file.write_text(json.dumps(result["recipe"], ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    else:
        recipe_file.unlink(missing_ok=True)
    result_file.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("slugs", nargs="*")
    parser.add_argument("--all", action="store_true", help="convert every dump")
    parser.add_argument("--no-retry", action="store_true", help="single shot: no correction rounds")
    parser.add_argument("--no-eval", action="store_true", help="skip the evaluator")
    parser.add_argument("--force", action="store_true", help="ignore cached results")
    parser.add_argument("--effort", help="extractor effort level (low, medium, high, ...)")
    parser.add_argument("--evaluator-effort", help="evaluator effort level")
    parser.add_argument("--jobs", type=int, default=4)
    parser.add_argument("--out", type=Path, default=OUT)
    args = parser.parse_args()

    slugs = sorted(p.name for p in DUMPS.iterdir() if p.is_dir()) if args.all else args.slugs
    if not slugs:
        parser.error("name at least one slug or use --all")
    config = Config(evaluate=not args.no_eval, extractor_effort=args.effort, evaluator_effort=args.evaluator_effort)
    if args.no_retry:
        config.max_schema_failures = config.max_evaluator_failures = 0

    with ThreadPoolExecutor(max_workers=args.jobs) as pool:
        results = list(pool.map(lambda slug: convert_dump(slug, config, args.out, args.force), slugs))
    for result in results:
        detail = result["reason"] or ""
        if result["rounds"]:
            last = result["rounds"][-1]
            detail = detail or "; ".join(last["schema_errors"] + last["findings"])
        print(f"{result['slug']:52} {result['status']:13} rounds={len(result['rounds'])} {detail[:150]}")
    counts = {status: sum(r["status"] == status for r in results) for status in sorted({r["status"] for r in results})}
    print(f"\n{len(results)} pages: {counts}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
