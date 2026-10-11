"""Reads the site's own JSON-LD Recipe as a loose reference for scoring. Development tooling only."""

import html
import json
import re
from dataclasses import dataclass, field
from pathlib import Path

from bs4 import BeautifulSoup


@dataclass
class Reference:
    name: str | None = None
    ingredients: list[str] = field(default_factory=list)
    steps: list[str] = field(default_factory=list)
    times: dict[str, str] = field(default_factory=dict)
    recipe_yield: str | None = None
    properties: set[str] = field(default_factory=set)


def _find_recipes(node):
    if isinstance(node, dict):
        types = node.get("@type")
        if "Recipe" in (types if isinstance(types, list) else [types]):
            yield node
        for value in node.values():
            yield from _find_recipes(value)
    elif isinstance(node, list):
        for value in node:
            yield from _find_recipes(value)


def _plain(text) -> str:
    text = html.unescape(str(text))
    if "<" in text:
        text = BeautifulSoup(text, "lxml").get_text(" ")
    return re.sub(r"\s+", " ", text).strip()


def _steps(node) -> list[str]:
    if node is None:
        return []
    if isinstance(node, str):
        return [s for s in (_plain(part) for part in re.split(r"\n+|</(?:p|li)>", node)) if s]
    if isinstance(node, list):
        return [step for item in node for step in _steps(item)]
    if isinstance(node, dict):
        if "itemListElement" in node:
            return _steps(node["itemListElement"])
        return _steps(node.get("text") or node.get("name"))
    return []


def load_reference(dump_dir: Path) -> Reference | None:
    page = (dump_dir / "page.html").read_text(encoding="utf-8", errors="replace")
    recipe = None
    for block in re.findall(r"<script[^>]*application/ld\+json[^>]*>(.*?)</script>", page, re.S | re.I):
        try:
            recipe = next(_find_recipes(json.loads(block, strict=False)), recipe)
        except json.JSONDecodeError:
            continue
    if recipe is None:
        return None

    recipe_yield = recipe.get("recipeYield")
    if isinstance(recipe_yield, list):
        recipe_yield = max((str(y) for y in recipe_yield), key=len, default=None)
    return Reference(
        name=_plain(recipe["name"]) if recipe.get("name") else None,
        ingredients=[_plain(i) for i in recipe.get("recipeIngredient") or [] if _plain(i)],
        steps=_steps(recipe.get("recipeInstructions")),
        times={k: recipe[k] for k in ("prepTime", "cookTime", "totalTime") if isinstance(recipe.get(k), str)},
        recipe_yield=str(recipe_yield) if recipe_yield not in (None, "") else None,
        properties={k for k, v in recipe.items() if v not in (None, "", [], {})},
    )
