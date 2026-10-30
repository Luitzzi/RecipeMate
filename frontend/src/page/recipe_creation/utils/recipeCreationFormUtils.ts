import {
  Cuisine,
  Diet,
  type IngredientUiDTO,
  RecipeProperty,
  type RecipeStepDTO,
  Unit,
} from "#/generated/interface-api.ts";
import type { UploadFile } from "antd";

export type RecipeStepDTODraft = Omit<RecipeStepDTO, "stepMethod"> &
  Partial<Pick<RecipeStepDTO, "stepMethod">>;

export type RecipeCreationFormValues = {
  recipeName: string;
  images: UploadFile[];
  ingredients: IngredientValues[];
  categories: CategoryValues;
  steps: RecipeStepDTODraft[];
};

export const RECIPE_CREATION_FORM_FIELDS = {
  recipeName: "recipeName",
  images: "images",
  ingredients: "ingredients",
  categories: "categories",
  steps: "steps",
} as const satisfies Record<
  keyof RecipeCreationFormValues,
  keyof RecipeCreationFormValues
>;

export const MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
export const ALLOWED_IMAGE_TYPES = ["image/jpeg", "image/png", "image/webp"];

/**
 * Validates an image before it is uploaded.
 * @returns The validation error message or undefined if the image is valid
 */
export const validateImage = (file: File): string | undefined => {
  if (!ALLOWED_IMAGE_TYPES.includes(file.type))
    return "Nur JPG, PNG oder WebP erlaubt";
  if (file.size > MAX_IMAGE_SIZE_BYTES) return "Datei ist größer als 5 MB";
  return undefined;
};

export const normalizeName = (name: string) => name.trim().toLowerCase();

export type IngredientValues = {
  ingredient: Partial<IngredientUiDTO>;
  amount?: number;
  unit: Unit;
};

export const INGREDIENT_VALUES_FIELDS = {
  ingredient: "ingredient",
  amount: "amount",
  unit: "unit",
} as const satisfies Record<keyof IngredientValues, keyof IngredientValues>;

export type CategoryValues = {
  diet: Diet;
  cuisine: Cuisine[];
  properties: RecipeProperty[];
};

export const CATEGORY_VALUES_FIELDS = {
  diet: "diet",
  cuisine: "cuisine",
  properties: "properties",
} as const satisfies Record<keyof CategoryValues, keyof CategoryValues>;

export const getEmptyIngredient = (): IngredientValues => ({
  ingredient: {},
  unit: Unit.GRAM,
});

export const getEmptyStep = (): RecipeStepDTODraft => ({
  description: "",
});
