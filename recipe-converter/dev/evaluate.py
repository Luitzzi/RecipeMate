"""Scores pipeline outputs against the sites' own JSON-LD. Development tooling only.

Usage: uv run python -m dev.evaluate [--out DIR] [--report FILE]
"""

import argparse
import json
from pathlib import Path

from dev.reference import load_reference
from textmatch import coverage, line_recall, tokens

ROOT = Path(__file__).resolve().parent.parent
COMPARED = ["description", "image", "author", "datePublished", "prepTime", "cookTime", "totalTime", "recipeYield",
            "recipeCategory", "recipeCuisine", "keywords", "nutrition"]  # fmt: skip


def step_texts(recipe: dict) -> list[str]:
    texts = []
    for item in recipe["recipeInstructions"]:
        texts += [s["text"] for s in item["itemListElement"]] if item["@type"] == "HowToSection" else [item["text"]]
    return texts


def score(slug: str, out: Path) -> dict:
    result = json.loads((out / slug / "result.json").read_text(encoding="utf-8"))
    row = {"slug": slug, "status": result["status"], "rounds": len(result["rounds"])}
    first = result["rounds"][0] if result["rounds"] else None
    if first:
        row["first_pass"] = not first["schema_errors"] and not first["findings"] and result["status"] != "no_recipe"
        row["findings"] = first["schema_errors"] + first["findings"]
        last = result["rounds"][-1]
        row["open_findings"] = (last["schema_errors"] + last["findings"]) if result["status"] != "ok" else []
        row["notes"] = last["notes"]
    row["cost"] = sum(u["cost_usd"] for u in result["usage"])
    recipe = result["recipe"]
    reference = load_reference(ROOT / "dumps" / slug)
    row["reference"] = reference is not None
    if recipe is None:
        row["reason"] = result["reason"]
        return row

    steps = step_texts(recipe)
    row["ingredients"] = len(recipe["recipeIngredient"])
    row["steps"] = len(steps)
    if reference:
        lines = [set(tokens(line)) for line in recipe["recipeIngredient"]]
        missing = [i for i in reference.ingredients if line_recall(i, lines, window=1) < 0.75]
        row["ref_ingredients"] = len(reference.ingredients)
        row["ref_steps"] = len(reference.steps)
        row["ingredient_recall"] = 1 - len(missing) / len(reference.ingredients) if reference.ingredients else None
        row["missing_ingredients"] = missing
        text = " ".join(steps)
        covered = [coverage(s, text, n=2) for s in reference.steps]
        row["instruction_coverage"] = sum(covered) / len(covered) if covered else None
        row["weak_steps"] = [s[:120] for s, c in zip(reference.steps, covered) if c < 0.6]
        row["missing_properties"] = [p for p in COMPARED if p in reference.properties and p not in recipe]
        row["extra_properties"] = [p for p in COMPARED if p in recipe and p not in reference.properties]
        row["time_mismatch"] = {
            k: (v, recipe[k]) for k, v in reference.times.items() if k in recipe and recipe[k] != v and v != "PT0M"
        }
    return row


def render(rows: list[dict]) -> str:
    def num(value):
        return "–" if value is None else f"{value:.2f}"

    lines = ["| Page | Status | First pass | Rounds | Ingredients (ref) | Steps (ref) | Ingr. recall | Instr. coverage | Missing vs. JSON-LD |",
             "|---|---|---|---|---|---|---|---|---|"]  # fmt: skip
    for row in rows:
        if "ingredients" not in row:
            lines.append(f"| {row['slug']} | {row['status']} | – | {row['rounds']} | – | – | – | – | {row.get('reason') or ''} |")
            continue
        ref = row["reference"]
        lines.append(
            f"| {row['slug']} | {row['status']} | {'yes' if row.get('first_pass') else 'no'} | {row['rounds']} "
            f"| {row['ingredients']} ({row['ref_ingredients'] if ref else '–'}) "
            f"| {row['steps']} ({row['ref_steps'] if ref else '–'}) "
            f"| {num(row.get('ingredient_recall'))} | {num(row.get('instruction_coverage'))} "
            f"| {', '.join(row.get('missing_properties', []))} |"
        )
    converted = [r for r in rows if "ingredients" in r]
    scored = [r for r in converted if r.get("ingredient_recall") is not None]
    summary = [
        f"- Pages: {len(rows)}, converted: {len(converted)}, no recipe: {sum(r['status'] == 'no_recipe' for r in rows)}, "
        f"failed/error: {sum(r['status'] in ('failed', 'error') for r in rows)}",
        f"- Good in one shot (schema-valid, no blocking finding): {sum(bool(r.get('first_pass')) for r in converted)}/{len(converted)}",
        f"- Good after the correction rounds: {sum(r['status'] == 'ok' for r in converted)}/{len(converted)}; "
        f"needs review: {sum(r['status'] == 'needs_review' for r in converted)}",
    ]
    if scored:
        summary += [
            f"- Mean ingredient recall vs. JSON-LD: {sum(r['ingredient_recall'] for r in scored) / len(scored):.3f}",
            f"- Mean instruction coverage vs. JSON-LD: "
            f"{sum(r['instruction_coverage'] or 0 for r in scored) / len(scored):.3f}",
            f"- Steps: {sum(r['steps'] for r in scored)} vs. {sum(r['ref_steps'] for r in scored)} in JSON-LD",
        ]
    summary.append(f"- Cost (list price): ${sum(r['cost'] for r in rows):.2f}")
    details = []
    for row in rows:
        notes = []
        if row.get("findings"):
            notes += [f"  - first-pass finding: {f}" for f in row["findings"]]
        if row.get("open_findings"):
            notes += [f"  - still open: {f}" for f in row["open_findings"]]
        if row.get("notes"):
            notes += [f"  - note: {f}" for f in row["notes"]]
        if row.get("missing_ingredients"):
            notes += [f"  - reference ingredient not matched: {i}" for i in row["missing_ingredients"]]
        if row.get("weak_steps"):
            notes += [f"  - reference step weakly covered: {s}" for s in row["weak_steps"]]
        if row.get("time_mismatch"):
            notes += [f"  - {k}: JSON-LD {a}, output {b}" for k, (a, b) in row["time_mismatch"].items()]
        if notes:
            details += [f"- {row['slug']}"] + notes
    return "\n".join(["## Summary", "", *summary, "", "## Pages", "", *lines, "", "## Details", "", *details]) + "\n"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--out", type=Path, default=ROOT / "out")
    parser.add_argument("--report", type=Path)
    args = parser.parse_args()
    slugs = sorted(p.parent.name for p in args.out.glob("*/result.json"))
    report = render([score(slug, args.out) for slug in slugs])
    (args.report or args.out / "report.md").write_text(report, encoding="utf-8")
    print(report)


if __name__ == "__main__":
    main()
