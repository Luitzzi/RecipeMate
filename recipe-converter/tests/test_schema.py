import copy

import pytest

from validation import schema_errors

VALID = {
    "@context": "https://schema.org",
    "@type": "Recipe",
    "name": "Soup",
    "inLanguage": "en-GB",
    "image": ["https://example.org/soup.jpg"],
    "author": [{"@type": "Person", "name": "A. Cook"}],
    "datePublished": "2024-03-01",
    "prepTime": "PT20M",
    "cookTime": "PT1H30M",
    "totalTime": "P1DT2H",
    "recipeYield": "4 servings",
    "keywords": ["soup"],
    "suitableForDiet": ["https://schema.org/VeganDiet"],
    "nutrition": {"@type": "NutritionInformation", "calories": "250 kcal"},
    "recipeIngredient": ["2 onions"],
    "recipeInstructions": [
        {"@type": "HowToStep", "text": "Slice the onions."},
        {
            "@type": "HowToSection",
            "name": "Soup",
            "itemListElement": [{"@type": "HowToStep", "text": "Boil.", "image": "https://example.org/1.jpg"}],
        },
    ],
}


def changed(path: list, value=..., delete: bool = False) -> dict:
    recipe = copy.deepcopy(VALID)
    node = recipe
    for key in path[:-1]:
        node = node[key]
    if delete:
        del node[path[-1]]
    else:
        node[path[-1]] = value
    return recipe


def test_valid_recipe():
    assert schema_errors(VALID, "recipe") == []


@pytest.mark.parametrize("key", ["@context", "@type", "name", "inLanguage", "recipeIngredient", "recipeInstructions"])
def test_required_properties(key):
    assert schema_errors(changed([key], delete=True), "recipe")


@pytest.mark.parametrize(
    "path, value",
    [
        (["aggregateRating"], {"ratingValue": 5}),
        (["description"], None),
        (["description"], " "),
        (["keywords"], []),
        (["keywords"], "soup, onion"),
        (["image"], "https://example.org/soup.jpg"),
        (["image"], ["images/000.jpg"]),
        (["author"], "A. Cook"),
        (["prepTime"], "20 minutes"),
        (["prepTime"], "PT"),
        (["prepTime"], "P"),
        (["datePublished"], "March 2024"),
        (["recipeYield"], 4),
        (["inLanguage"], "English"),
        (["suitableForDiet"], ["vegan"]),
        (["nutrition"], {"@type": "NutritionInformation"}),
        (["nutrition", "calories"], 250),
        (["recipeIngredient"], [""]),
        (["recipeInstructions"], "Slice the onions."),
        (["recipeInstructions"], ["Slice the onions."]),
        (["recipeInstructions"], []),
        (["recipeInstructions", 0, "text"], ""),
        (["recipeInstructions", 0, "position"], 1),
        (["recipeInstructions", 0, "@type"], "Step"),
        (["recipeInstructions", 1, "itemListElement"], []),
        (["recipeInstructions", 1, "itemListElement", 0], {"@type": "HowToSection", "name": "x", "itemListElement": []}),
    ],
)
def test_rejected(path, value):
    assert schema_errors(changed(path, value), "recipe")


def test_error_messages_name_the_place():
    errors = schema_errors(changed(["recipeInstructions", 0, "position"], 1), "recipe")
    assert errors == ["$.recipeInstructions[0]: Additional properties are not allowed ('position' was unexpected)"]


def test_non_object_answer():
    assert schema_errors([VALID], "recipe") == ["$: expected a JSON object, got list"]
    assert schema_errors(None, "recipe")
