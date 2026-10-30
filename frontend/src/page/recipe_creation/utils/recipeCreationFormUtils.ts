import {
  Cuisine,
  Diet,
  type IngredientUiDTO,
  RecipeProperty,
  type RecipeStepDTOUnion,
  Unit,
} from "#/generated/interface-api.ts";
import type { UploadFile } from "antd";

export type UploadedImageResponse = {
  key: string;
};

export type RecipeCreationFormValues = {
  title?: string;
  description?: string;
  cookTimeInMinutes?: number;
  difficulty?: number;
  note?: string;
  sourceUrl?: string;
  images: UploadFile<UploadedImageResponse>[];
  ingredients: IngredientFormValues[];
  categories: CategoryValues;
  steps: Partial<RecipeStepDTOUnion>[];
};

export type IngredientFormValues = {
  ingredient: Partial<IngredientUiDTO>;
  amount?: number;
  unit: Unit;
};

export type CategoryValues = {
  diet: Diet;
  cuisine: Cuisine[];
  properties: RecipeProperty[];
};

export const RECIPE_CREATION_FORM_FIELDS = {
  title: "title",
  description: "description",
  cookTimeInMinutes: "cookTimeInMinutes",
  difficulty: "difficulty",
  note: "note",
  sourceUrl: "sourceUrl",
  images: "images",
  ingredients: "ingredients",
  categories: "categories",
  steps: "steps",
} as const satisfies Record<
  keyof RecipeCreationFormValues,
  keyof RecipeCreationFormValues
>;

export const INGREDIENT_VALUES_FIELDS = {
  ingredient: "ingredient",
  amount: "amount",
  unit: "unit",
} as const satisfies Record<
  keyof IngredientFormValues,
  keyof IngredientFormValues
>;

export const CATEGORY_VALUES_FIELDS = {
  diet: "diet",
  cuisine: "cuisine",
  properties: "properties",
} as const satisfies Record<keyof CategoryValues, keyof CategoryValues>;

export const getEmptyIngredient = (): IngredientFormValues => ({
  ingredient: {},
  unit: Unit.GRAM,
});
