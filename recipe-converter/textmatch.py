"""Fuzzy text comparison helpers."""

import re
import unicodedata

DECIMALS = {r"0?[.,]5": "1/2", r"0?[.,]25": "1/4", r"0?[.,]75": "3/4", r"0?[.,]33\d*": "1/3", r"0?[.,]6[67]\d*": "2/3"}
FRACTIONS = str.maketrans({"⁄": "/", "½": " 1/2", "¼": " 1/4", "¾": " 3/4", "⅓": " 1/3", "⅔": " 2/3", "⅛": " 1/8"})


def tokens(text: str) -> list[str]:
    text = unicodedata.normalize("NFKC", text.translate(FRACTIONS)).lower()
    for decimal, fraction in DECIMALS.items():
        text = re.sub(rf"(?<![\d.,]){decimal}(?!\d)", fraction, text)
    return re.findall(r"\d+|[^\W\d_]+", text)


def _same(a: str, b: str) -> bool:
    """Equal, or one is the other's stem (singular/plural, inflection)."""
    return a == b or (min(len(a), len(b)) >= 4 and (a.startswith(b) or b.startswith(a)))


def line_recall(needle: str, haystack_lines: list[list[str]], window: int = 3) -> float:
    """Best share of the needle's tokens found within `window` consecutive haystack lines."""
    wanted = set(tokens(needle))
    if not wanted:
        return 1.0
    best = 0.0
    for i in range(len(haystack_lines)):
        seen = set().union(*haystack_lines[i : i + window])
        found = sum(1 for w in wanted if w in seen or any(_same(w, s) for s in seen))
        best = max(best, found / len(wanted))
        if best == 1.0:
            break
    return best


def shingles(text: str, n: int) -> set[tuple[str, ...]]:
    toks = tokens(text)
    if len(toks) < n:
        return {tuple(toks)} if toks else set()
    return {tuple(toks[i : i + n]) for i in range(len(toks) - n + 1)}


def coverage(needle: str, haystack: str, n: int = 3) -> float:
    """Share of the needle's word n-grams that occur in the haystack."""
    wanted = shingles(needle, n)
    if not wanted:
        return 1.0
    if len(next(iter(wanted))) < n:
        return float(set(next(iter(wanted))) <= set(tokens(haystack)))
    return len(wanted & shingles(haystack, n)) / len(wanted)
