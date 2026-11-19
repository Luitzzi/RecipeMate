import type {
  Cuisine,
  Diet,
  HeatLevel,
  OvenMode,
  RecipeProperty,
  StepMethod,
  Unit,
} from "#/generated/interface-api.ts";

export type EnumTranslations = {
  cuisine: Record<Cuisine, string>;
  recipeProperty: Record<RecipeProperty, string>;
  diet: Record<Diet, string>;
  difficulty: Record<"1" | "2" | "3" | "fallback", string>;
  unit: Record<`${Unit}_one` | `${Unit}_other`, string>;
  stepMethod: Record<StepMethod, string>;
  ovenMode: Record<OvenMode, string>;
  heatLevel: Record<HeatLevel, string>;
  minutes: string;
};
