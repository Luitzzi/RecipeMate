"""Checks that the reduced markdown still holds the recipe, using the JSON-LD reference. No model calls.

Usage: uv run python -m dev.check_reduce [slug ...]
"""

import sys
from pathlib import Path

from dev.reference import load_reference
from textmatch import coverage, line_recall, tokens
from reduce import MAX_CHARS, reduce_dump

DUMPS = Path(__file__).resolve().parent.parent / "dumps"
TARGET = 0.95


def main(slugs: list[str]) -> int:
    dumps = [DUMPS / s for s in slugs] if slugs else sorted(p for p in DUMPS.iterdir() if p.is_dir())
    failed = 0
    total_chars = 0
    print(f"{'slug':52} {'chars':>7} {'ingr':>6} {'steps':>6}")
    for dump in dumps:
        markdown = reduce_dump(dump)
        total_chars += len(markdown)
        reference = load_reference(dump)
        ingredients = steps = None
        if reference:
            lines = [set(tokens(line)) for line in markdown.splitlines() if line.strip()]
            hits = [line_recall(i, lines) >= 0.75 for i in reference.ingredients]
            ingredients = sum(hits) / len(hits) if hits else None
            if reference.steps:
                steps = sum(coverage(s, markdown) for s in reference.steps) / len(reference.steps)
        bad = len(markdown) >= MAX_CHARS or any(v is not None and v < TARGET for v in (ingredients, steps))
        failed += bad
        fmt = lambda v: "   n/a" if v is None else f"{v:6.2f}"  # noqa: E731
        print(f"{dump.name:52} {len(markdown):7} {fmt(ingredients)} {fmt(steps)} {'FAIL' if bad else ''}")
    print(f"\n{len(dumps)} pages, {total_chars} chars, {failed} below target")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
