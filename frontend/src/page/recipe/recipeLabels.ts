import {
  Diet,
  HeatLevel,
  OvenMode,
  Unit,
  type RecipeStepUiDTOUnion,
} from "#/generated/interface-api.ts";

/** German display labels for the recipe enums of the backend. */

export const dietLabels: Record<Diet, string> = {
  [Diet.VEGAN]: "Vegan",
  [Diet.VEGETARIAN]: "Vegetarisch",
  [Diet.PESCATARIAN]: "Pescetarisch",
  [Diet.OMNIVORE]: "Omnivor",
};

// Assumption: difficulty is rated from 1 (easy) to 3 (hard)
const difficultyLabels: Record<number, string> = {
  1: "Einfach",
  2: "Mittel",
  3: "Schwer",
};

export const getDifficultyLabel = (difficulty: number) =>
  difficultyLabels[difficulty] ?? `Stufe ${difficulty}`;

export const unitLabels: Record<Unit, string> = {
  [Unit.GRAM]: "g",
  [Unit.KILOGRAM]: "kg",
  [Unit.MILLILITER]: "ml",
  [Unit.LITER]: "l",
  [Unit.TEASPOON]: "TL",
  [Unit.TABLESPOON]: "EL",
  [Unit.CUP]: "Tasse",
  [Unit.PIECE]: "Stück",
  [Unit.CLOVE]: "Zehe",
  [Unit.SLICE]: "Scheibe",
  [Unit.BUNCH]: "Bund",
  [Unit.SPRING]: "Zweig",
  [Unit.LEAF]: "Blatt",
  [Unit.CAN]: "Dose",
  [Unit.PACKAGE]: "Pck.",
  [Unit.CUP_CONTAINER]: "Becher",
  [Unit.CUBE]: "Würfel",
  [Unit.PINCH]: "Prise",
  [Unit.KNIFE_TIP]: "Msp.",
  [Unit.DASH]: "Schuss",
  [Unit.TO_TASTE]: "nach Geschmack",
};

export const stepMethodLabels: Record<
  RecipeStepUiDTOUnion["stepMethod"],
  string
> = {
  PREPARE: "Vorbereitung",
  STOVE: "Herd",
  OVEN: "Backofen",
};

export const ovenModeLabels: Record<OvenMode, string> = {
  [OvenMode.TOP_BOTTOM_HEAT]: "Ober-/Unterhitze",
  [OvenMode.CIRCULATING_AIR]: "Umluft",
};

export const heatLevelLabels: Record<HeatLevel, string> = {
  [HeatLevel.LOW]: "Niedrige Hitze",
  [HeatLevel.MEDIUM]: "Mittlere Hitze",
  [HeatLevel.HIGH]: "Hohe Hitze",
};

const amountFormat = new Intl.NumberFormat("de-DE", {
  maximumFractionDigits: 2,
});

export const formatAmount = (amount: number | undefined, unit: Unit) => {
  if (amount == null) return unitLabels[unit];
  return `${amountFormat.format(amount)} ${unitLabels[unit]}`;
};

export const formatMinutes = (minutes: number) => `${minutes} Min.`;
