import {
  Cuisine,
  Diet,
  type IngredientUiDTO,
  type Language,
  RecipeProperty,
} from "#/generated/interface-api.ts";

export type RecipeSearchFormValues = {
  title?: string;
  language: Language;
  ingredients: IngredientUiDTO[];
  diet?: Diet;
  cookingTime?: number;
  difficulty?: number;
  cuisines?: Cuisine[];
  properties?: RecipeProperty[];
  filterByFolder?: boolean;
  folderName?: string;
};

export const RECIPE_SEARCH_FORM_VALUES_FIELDS = {
  title: "title",
  language: "language",
  ingredients: "ingredients",
  diet: "diet",
  cookingTime: "cookingTime",
  difficulty: "difficulty",
  cuisines: "cuisines",
  properties: "properties",
  filterByFolder: "filterByFolder",
  folderName: "folderName",
} as const satisfies Record<
  keyof RecipeSearchFormValues,
  keyof RecipeSearchFormValues
>;
