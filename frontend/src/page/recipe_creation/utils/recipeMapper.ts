import type {
  IngredientFormValues,
  RecipeCreationFormValues,
} from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import {
  HeatLevel,
  type IngredientUiDTO,
  OvenMode,
  type OvenStepDTO,
  type PrepareStepDTO,
  type RecipeIngredientUiDTO,
  type RecipeStepDTOUnion,
  type RecipeUiDTO,
  StepMethod,
} from "#/generated/interface-api.ts";

export const toRecipeUiDTO = (
  formValues: RecipeCreationFormValues,
): RecipeUiDTO => ({
  title: formValues.title as string,
  description: formValues.description as string,
  cookTimeInMinutes: formValues.cookTimeInMinutes as number,
  difficulty: formValues.difficulty as number,
  ingredients: formValues.ingredients.map(toRecipeIngredient),
  imageKeys: formValues.images
    .filter((image) => image.response !== undefined)
    .flatMap((image) => image.response!.key),
  note: formValues.note as string,
  sourceUrl: formValues.sourceUrl,
  steps: formValues.steps.map(toRecipeStep),

  diet: formValues.categories.diet,
  cuisines: formValues.categories.cuisine,
  properties: formValues.categories.properties,
});

const toRecipeIngredient = (
  ingredientFormValues: IngredientFormValues,
): RecipeIngredientUiDTO => ({
  ingredient: ingredientFormValues.ingredient as IngredientUiDTO,
  amount: ingredientFormValues.amount,
  unit: ingredientFormValues.unit,
});

const toRecipeStep = (
  step: Partial<RecipeStepDTOUnion>,
): RecipeStepDTOUnion => {
  switch (step.stepMethod) {
    case StepMethod.PREPARE:
      return {
        stepMethod: step.stepMethod,
        description: step.description as string,
      } satisfies PrepareStepDTO;
    case StepMethod.OVEN:
      return {
        stepMethod: step.stepMethod,
        description: step.description as string,
        temperatureC: step.temperatureC as number,
        ovenMode: step.ovenMode as OvenMode,
        durationSeconds: step.durationSeconds,
        preheat: step.preheat as boolean,
      } satisfies OvenStepDTO;
    case StepMethod.STOVE:
      return {
        stepMethod: step.stepMethod,
        description: step.description as string,
        heatLevel: step.heatLevel as HeatLevel,
      };
    default:
      throw new Error("Unhandled step discriminator: " + step.stepMethod);
  }
};
