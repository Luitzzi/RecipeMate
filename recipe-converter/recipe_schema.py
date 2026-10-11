"""Structured recipe format produced by the converter.

All fields are required in the JSON: unknown values are ``null`` (or ``[]``), never omitted.
"""

from typing import Annotated, Literal, Self

from pydantic import BaseModel, ConfigDict, Field, model_validator

SCHEMA_VERSION = 1

NonEmptyStr = Annotated[str, Field(min_length=1)]
Minutes = Annotated[int, Field(ge=0)]
PositiveNumber = Annotated[float, Field(gt=0)]

Unit = Literal[
    "g",
    "kg",
    "ml",
    "l",
    "tsp",
    "tbsp",
    "cup",
    "oz",
    "lb",
    "piece",
    "pinch",
    "clove",
    "can",
    "package",
    "bunch",
    "slice",
]


class Model(BaseModel):
    model_config = ConfigDict(
        extra="forbid",
        str_strip_whitespace=True,
        populate_by_name=True,
        serialize_by_alias=True,
    )


class Source(Model):
    url: NonEmptyStr
    site_name: NonEmptyStr | None
    author: NonEmptyStr | None


class Yield(Model):
    quantity: PositiveNumber | None
    unit: NonEmptyStr | None = Field(description='Free text in the source language, e.g. "servings", "cookies".')
    original: NonEmptyStr = Field(description="Yield exactly as written on the page.")


class Times(Model):
    prep_minutes: Minutes | None
    cook_minutes: Minutes | None
    total_minutes: Minutes | None = Field(
        description="As stated by the source, not computed; may exceed prep + cook (resting, chilling)."
    )


class Measure(Model):
    quantity: PositiveNumber
    unit: Unit | None


class Ingredient(Model):
    original: NonEmptyStr = Field(description="Ingredient line exactly as written on the page.")
    quantity: PositiveNumber | None = Field(description='Fractions as decimals: "1 ½" -> 1.5.')
    quantity_max: PositiveNumber | None = Field(description="Upper bound, only for ranges such as 2-3 tbsp.")
    unit: Unit | None = Field(description="Mapped to the fixed list, never converted between systems.")
    name: NonEmptyStr = Field(description="Ingredient without quantity, unit or preparation.")
    note: NonEmptyStr | None = Field(description='Preparation and qualifiers, e.g. "sifted", "at room temperature".')
    optional: bool
    alternatives: list[Measure] = Field(
        description='Further measures for the same ingredient: "1 cup (120 g) flour" -> [{120, "g"}].'
    )

    @model_validator(mode="after")
    def _check_quantities(self) -> Self:
        if self.quantity is None:
            if self.quantity_max is not None:
                raise ValueError("quantity_max requires quantity")
            if self.unit is not None:
                raise ValueError("unit requires quantity")
        elif self.quantity_max is not None and self.quantity_max <= self.quantity:
            raise ValueError("quantity_max must be greater than quantity")
        return self


class IngredientGroup(Model):
    name: NonEmptyStr | None = Field(description="Section heading, null for a recipe without sections.")
    ingredients: list[Ingredient] = Field(min_length=1)


class Step(Model):
    text: NonEmptyStr = Field(description="One action block as presented by the source, without step number.")
    image: NonEmptyStr | None = Field(description="Path of a dumped image, relative to the dump folder.")


class InstructionGroup(Model):
    name: NonEmptyStr | None
    steps: list[Step] = Field(min_length=1)


class Recipe(Model):
    title: NonEmptyStr
    description: NonEmptyStr | None
    language: str = Field(pattern=r"^[a-z]{2,3}(-[A-Za-z0-9]{2,8})*$", description="BCP 47 code of the recipe text.")
    source: Source
    yield_: Yield | None = Field(alias="yield")
    times: Times
    ingredient_groups: list[IngredientGroup] = Field(min_length=1)
    instruction_groups: list[InstructionGroup] = Field(min_length=1)
    notes: list[NonEmptyStr]
    image: NonEmptyStr | None = Field(description="Path of the main dumped image, relative to the dump folder.")
    categories: list[NonEmptyStr]
    cuisine: NonEmptyStr | None
    diet: list[NonEmptyStr]

    @model_validator(mode="after")
    def _check_group_names(self) -> Self:
        for kind, groups in (("ingredient", self.ingredient_groups), ("instruction", self.instruction_groups)):
            if len(groups) > 1 and any(group.name is None for group in groups):
                raise ValueError(f"{kind} groups need a name when there is more than one")
        return self


class ConversionResult(Model):
    schema_version: Literal[1] = SCHEMA_VERSION
    status: Literal["ok", "not_a_recipe"]
    reason: NonEmptyStr | None = Field(description="Why the page is not a recipe; null when status is ok.")
    recipe: Recipe | None

    @model_validator(mode="after")
    def _check_status(self) -> Self:
        if self.status == "ok":
            if self.recipe is None:
                raise ValueError("status ok requires a recipe")
            if self.reason is not None:
                raise ValueError("status ok must not have a reason")
        else:
            if self.recipe is not None:
                raise ValueError("status not_a_recipe must not have a recipe")
            if self.reason is None:
                raise ValueError("status not_a_recipe requires a reason")
        return self
