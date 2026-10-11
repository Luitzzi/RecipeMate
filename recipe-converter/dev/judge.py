"""Runs the evaluator on existing pipeline outputs and stores the verdicts. Development tooling only.

Usage: uv run python -m dev.judge OUT_DIR [--name NAME] [--effort LEVEL] [--jobs N] [slug ...]
"""

import argparse
import json
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

import llm
from pipeline import Config, _evaluate, rule_findings, verdict_findings


def judge(directory: Path, config: Config, name: str, recipe_file: str = "recipe.json") -> dict | None:
    target = directory / f"{name}.json"
    if target.exists():
        return json.loads(target.read_text(encoding="utf-8"))
    if not (directory / recipe_file).exists():
        return None
    markdown = (directory / "page.md").read_text(encoding="utf-8")
    recipe = json.loads((directory / recipe_file).read_text(encoding="utf-8"))
    usage: list[dict] = []
    verdict, raw = _evaluate(markdown, recipe, config, llm.complete, usage)
    findings, notes = rule_findings(recipe, markdown)
    if verdict:
        blocking, remarks = verdict_findings(verdict, recipe, markdown)
        findings, notes = findings + blocking, notes + remarks
    else:
        findings.append("no readable verdict")
    record = {"slug": directory.name, "good": not findings, "findings": findings, "notes": notes,
              "verdict": verdict, "raw": raw, "usage": usage}  # fmt: skip
    target.write_text(json.dumps(record, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return record


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("out", type=Path)
    parser.add_argument("slugs", nargs="*")
    parser.add_argument("--name", default="verdict")
    parser.add_argument("--effort")
    parser.add_argument("--jobs", type=int, default=5)
    args = parser.parse_args()
    config = Config(evaluator_effort=args.effort)
    directories = [args.out / s for s in args.slugs] or sorted(p.parent for p in args.out.glob("*/recipe.json"))
    with ThreadPoolExecutor(max_workers=args.jobs) as pool:
        records = [r for r in pool.map(lambda d: judge(d, config, args.name), directories) if r]
    for record in records:
        print(f"{record['slug']:50} {'good' if record['good'] else 'NOT GOOD'}")
        for finding in record["findings"]:
            print(f"    {finding[:260]}")
        for note in record.get("notes", []):
            print(f"    (note) {note[:240]}")
    cost = sum(u["cost_usd"] for r in records for u in r["usage"])
    print(f"\n{sum(r['good'] for r in records)}/{len(records)} good, cost ${cost:.2f}")


if __name__ == "__main__":
    main()
