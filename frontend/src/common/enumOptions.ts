import { toSelectOptions } from "#/common/components/input/SelectOption.ts";
import {
  HeatLevel,
  OvenMode,
  StepMethod,
  Unit,
} from "#/generated/interface-api.ts";

const UNIT_LABELS: Record<Unit, string> = {
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
  [Unit.PACKAGE]: "Packung",
  [Unit.CUP_CONTAINER]: "Becher",
  [Unit.CUBE]: "Würfel",
  [Unit.PINCH]: "Prise",
  [Unit.KNIFE_TIP]: "Messerspitze",
  [Unit.DASH]: "Schuss",
  [Unit.TO_TASTE]: "Nach Geschmack",
};

export const UNIT_OPTIONS = toSelectOptions(
  Object.keys(UNIT_LABELS) as Unit[],
  (unit) => unit,
  (unit) => UNIT_LABELS[unit],
);

const STEP_METHOD_LABELS: Record<StepMethod, string> = {
  [StepMethod.PREPARE]: "prepare",
  [StepMethod.OVEN]: "oven",
  [StepMethod.STOVE]: "stove",
};

export const STEP_OPTIONS = toSelectOptions(
  Object.keys(STEP_METHOD_LABELS) as StepMethod[],
  (method) => method,
  (method) => STEP_METHOD_LABELS[method],
);

const OVEN_MODE_LABELS: Record<OvenMode, string> = {
  [OvenMode.CIRCULATING_AIR]: "Circulating air",
  [OvenMode.TOP_BOTTOM_HEAT]: "Top bottom heat",
};

export const OVEN_MODE_OPTIONS = toSelectOptions(
  Object.keys(OVEN_MODE_LABELS) as OvenMode[],
  (mode) => mode,
  (mode) => OVEN_MODE_LABELS[mode],
);

const HEAT_LEVEL_LABELS: Record<HeatLevel, string> = {
  [HeatLevel.LOW]: "Low",
  [HeatLevel.MEDIUM]: "Medium",
  [HeatLevel.HIGH]: "High",
};

export const HEAT_LEVEL_OPTIONS = toSelectOptions(
  Object.keys(HEAT_LEVEL_LABELS) as HeatLevel[],
  (level) => level,
  (level) => HEAT_LEVEL_LABELS[level],
);
