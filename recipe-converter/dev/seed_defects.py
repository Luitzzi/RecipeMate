"""Measures the evaluator: detection of seeded defects, false alarms and stability. Development tooling only.

Takes pipeline outputs with status "ok" as accepted recipes, injects one defect per blocking question and runs the
evaluator on each variant. The unmodified recipes are evaluated twice more for false alarms and stability.

Usage: uv run python -m dev.seed_defects OUT_DIR [--limit N] [--jobs N]
"""

import argparse
import copy
import json
import re
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

import llm
from pipeline import BLOCKING_CHECKS, Config, _evaluate, _steps, rule_findings, verdict_findings


def _middle(items: list) -> int:
    return len(items) // 2


def drop_ingredient(recipe: dict) -> str:
    return f"removed ingredient: {recipe['recipeIngredient'].pop(_middle(recipe['recipeIngredient']))}"


def remove_step(recipe: dict) -> str:
    for item in recipe["recipeInstructions"]:
        steps = item["itemListElement"] if item["@type"] == "HowToSection" else None
        if steps and len(steps) > 2:
            return f"removed step: {steps.pop(_middle(steps))['text']}"
    steps = recipe["recipeInstructions"]
    return f"removed step: {steps.pop(_middle(steps)).get('text')}"


def add_ingredient(recipe: dict) -> str:
    recipe["recipeIngredient"].insert(_middle(recipe["recipeIngredient"]), "2 g Xanthan")
    return "added ingredient: 2 g Xanthan"


def change_amount(recipe: dict) -> str:
    """Swaps a number in a step for another number of the recipe, so the code check for unknown numbers stays quiet."""
    numbers = re.findall(r"\d+", " ".join(recipe["recipeIngredient"]))
    for step in _steps(recipe):
        match = re.search(r"\d+", step["text"])
        other = next((n for n in numbers if match and n != match.group() and abs(int(n) - int(match.group())) > 1), None)
        if other:
            old = step["text"]
            step["text"] = old[: match.start()] + other + old[match.end() :]
            return f"changed {match.group()} to {other} in: {old}"
    raise ValueError("no step with a number")


def translate_step(recipe: dict) -> str:
    steps = _steps(recipe)
    step = steps[_middle(steps)]
    old = step["text"]
    step["text"] = (
        "Alles gut verrühren und zugedeckt beiseite stellen."
        if recipe["inLanguage"].startswith("en")
        else "Stir everything well, cover and set aside."
    )
    return f"replaced by a sentence in another language: {old}"


def heading_in_ingredients(recipe: dict) -> str:
    recipe["recipeIngredient"].insert(_middle(recipe["recipeIngredient"]), "Print Recipe – Shop these ingredients:")
    return "inserted a non-ingredient line into recipeIngredient"


def merge_steps(recipe: dict) -> str:
    for item in [*recipe["recipeInstructions"], None]:
        steps = recipe["recipeInstructions"] if item is None else item.get("itemListElement")
        if steps and len(steps) >= 5 and all(s["@type"] == "HowToStep" for s in steps):
            start = max(_middle(steps) - 2, 0)
            merged = " ".join(s["text"] for s in steps[start : start + 4])
            steps[start : start + 4] = [{"@type": "HowToStep", "text": merged}]
            return f"merged four steps: {merged[:120]}"
    raise ValueError("no run of five steps")


def strip_object(recipe: dict) -> str:
    """Cuts a step down to its first two words, so it no longer says what to do with what."""
    steps = _steps(recipe)
    step = steps[_middle(steps) - 1]
    old = step["text"]
    step["text"] = " ".join(old.split()[:2]).rstrip(",.;:") + "."
    return f"step cut down to '{step['text']}': {old[:120]}"


def ad_sentence(recipe: dict) -> str:
    steps = _steps(recipe)
    steps[_middle(steps)]["text"] += " Jump to Recipe · Pin · Rate this recipe ★★★★★ · Leave a comment"
    return "appended button labels to a step"


# Defect -> the question that should answer "no".
DEFECTS = {
    drop_ingredient: "ingredients_complete",
    remove_step: "instructions_complete",
    add_ingredient: "nothing_invented",
    change_amount: "values_match",
    translate_step: "language_kept",
    heading_in_ingredients: "ingredient_lines",
    merge_steps: "step_focus",
    strip_object: "steps_clear",
    ad_sentence: "no_foreign_text",
}


def audit(directory: Path, name: str, recipe: dict, config: Config, extra: dict) -> dict:
    target = directory / "calibration" / f"{name}.json"
    if target.exists():
        return json.loads(target.read_text(encoding="utf-8"))
    markdown = (directory / "page.md").read_text(encoding="utf-8")
    usage: list[dict] = []
    verdict, raw = _evaluate(markdown, recipe, config, llm.complete, usage)
    blocking, notes = verdict_findings(verdict, recipe, markdown) if verdict else (["no readable verdict"], [])
    record = {
        "slug": directory.name, "case": name, **extra,
        "evaluator_findings": blocking, "notes": notes, "code_findings": rule_findings(recipe, markdown)[0],
        "failed_checks": sorted({c["id"] for c in verdict["checks"] if c["answer"] == "no"} & BLOCKING_CHECKS) if verdict else [],
        "cost": sum(u["cost_usd"] for u in usage), "raw": raw,
    }  # fmt: skip
    target.parent.mkdir(exist_ok=True)
    target.write_text(json.dumps(record, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return record


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("out", type=Path)
    parser.add_argument("--limit", type=int, default=5)
    parser.add_argument("--jobs", type=int, default=2)
    args = parser.parse_args()
    config = Config()

    accepted = [
        p.parent for p in sorted(args.out.glob("*/result.json")) if json.loads(p.read_text(encoding="utf-8"))["status"] == "ok"
    ][: args.limit]
    jobs = []
    for directory in accepted:
        recipe = json.loads((directory / "recipe.json").read_text(encoding="utf-8"))
        for run in (1, 2):
            jobs.append((directory, f"clean_{run}", recipe, {"defect": None}))
        for defect, check in DEFECTS.items():
            variant = copy.deepcopy(recipe)
            try:
                description = defect(variant)
            except ValueError:
                continue
            jobs.append((directory, defect.__name__, variant, {"defect": defect.__name__, "expected": check, "seeded": description}))

    with ThreadPoolExecutor(max_workers=args.jobs) as pool:
        records = list(pool.map(lambda job: audit(job[0], job[1], job[2], config, job[3]), jobs))

    clean = [r for r in records if r["defect"] is None]
    seeded = [r for r in records if r["defect"]]
    print("## False alarms and stability (accepted recipes, two runs each)\n")
    for record in clean:
        print(f"- {record['slug']} {record['case']}: {'pass' if not record['evaluator_findings'] else 'FALSE ALARM'}")
        for finding in record["evaluator_findings"]:
            print(f"    {finding[:300]}")
    passes = sum(not r["evaluator_findings"] for r in clean)
    by_slug = {}
    for record in clean:
        by_slug.setdefault(record["slug"], []).append(not record["evaluator_findings"])
    stable = sum(len(set(v)) == 1 for v in by_slug.values())
    print(f"\nPass rate on accepted recipes: {passes}/{len(clean)}; same verdict in both runs: {stable}/{len(by_slug)}\n")

    print("## Detection of seeded defects\n")
    print("| Defect | Expected question | Evaluator detected | Expected question said no | Code check detected |")
    print("|---|---|---|---|---|")
    for defect, check in DEFECTS.items():
        rows = [r for r in seeded if r["defect"] == defect.__name__]
        if rows:
            print(
                f"| {defect.__name__} | {check} | {sum(bool(r['evaluator_findings']) for r in rows)}/{len(rows)} "
                f"| {sum(check in r['failed_checks'] for r in rows)}/{len(rows)} "
                f"| {sum(bool(r['code_findings']) for r in rows)}/{len(rows)} |"
            )
    detected = sum(bool(r["evaluator_findings"]) for r in seeded)
    either = sum(bool(r["evaluator_findings"] or r["code_findings"]) for r in seeded)
    print(f"\nEvaluator: {detected}/{len(seeded)}; evaluator or code check: {either}/{len(seeded)}")
    print(f"Cost: ${sum(r['cost'] for r in records):.2f}")
    missed = [r for r in seeded if not r["evaluator_findings"]]
    if missed:
        print("\nMissed by the evaluator:")
        for record in missed:
            print(f"- {record['slug']} {record['defect']}: {record['seeded'][:160]}")


if __name__ == "__main__":
    main()
