import type { RecipeFilterUiDTO } from "#/generated/interface-api.ts";
import type { RecipeSearchFormValues } from "#/page/recipe_search/utils/recipeSearchFormUtils.ts";

export const toRecipeFilterUiDTO = (
  formValues: RecipeSearchFormValues,
): RecipeFilterUiDTO => ({
  title: formValues.title,
  language: formValues.language,
  ingredientIds: formValues.ingredients.flatMap(
    (ingredient) => ingredient.id ?? [],
  ),
  diet: formValues.diet,
  maxCookTimeInMinutes: formValues.cookingTime,
  maxDifficulty: formValues.difficulty,
  // Not part of the form values while the advanced options are hidden
  cuisines: formValues.cuisines ?? [],
  properties: formValues.properties ?? [],
  folderName: formValues.filterByFolder
    ? formValues.folderName?.trim() || undefined
    : undefined,
});
