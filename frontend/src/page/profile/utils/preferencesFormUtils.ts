import {
  Allergen,
  CookingSkill,
  Cuisine,
  Diet,
  type IngredientUiDTO,
  Language,
  type UserUiDTO,
} from "#/generated/interface-api.ts";

export type PreferencesFormValues = Pick<
  UserUiDTO,
  "diet" | "cookingSkill" | "preferredCuisines" | "allergens"
> & {
  // The select works with ids, the DTO with whole ingredients
  dislikedIngredientIds: number[];
};

export const PREFERENCES_FORM_FIELDS = {
  diet: "diet",
  cookingSkill: "cookingSkill",
  preferredCuisines: "preferredCuisines",
  allergens: "allergens",
  dislikedIngredientIds: "dislikedIngredientIds",
} as const satisfies Record<
  keyof PreferencesFormValues,
  keyof PreferencesFormValues
>;

// const enums can't be iterated, the Record makes the compiler report missing values
const keysOf = <T extends string>(record: Record<T, true>) =>
  Object.keys(record) as T[];

export const DIETS = keysOf<Diet>({
  [Diet.OMNIVORE]: true,
  [Diet.PESCATARIAN]: true,
  [Diet.VEGETARIAN]: true,
  [Diet.VEGAN]: true,
});

export const COOKING_SKILLS = keysOf<CookingSkill>({
  [CookingSkill.BEGINNER]: true,
  [CookingSkill.INTERMEDIATE]: true,
  [CookingSkill.ADVANCED]: true,
  [CookingSkill.PROFESSIONAL]: true,
});

export const ALLERGENS = keysOf<Allergen>({
  [Allergen.GLUTEN]: true,
  [Allergen.CRUSTACEANS]: true,
  [Allergen.EGGS]: true,
  [Allergen.FISH]: true,
  [Allergen.PEANUTS]: true,
  [Allergen.SOY]: true,
  [Allergen.MILK]: true,
  [Allergen.TREE_NUTS]: true,
  [Allergen.CELERY]: true,
  [Allergen.MUSTARD]: true,
  [Allergen.SESAME]: true,
  [Allergen.SULPHITES]: true,
  [Allergen.LUPIN]: true,
  [Allergen.MOLLUSCS]: true,
  [Allergen.LACTOSE]: true,
});

export const CUISINES = keysOf<Cuisine>({
  [Cuisine.AMERICAN]: true,
  [Cuisine.ARGENTINIAN]: true,
  [Cuisine.AUSTRIAN]: true,
  [Cuisine.BRAZILIAN]: true,
  [Cuisine.BRITISH]: true,
  [Cuisine.CARIBBEAN]: true,
  [Cuisine.CHINESE]: true,
  [Cuisine.EASTERN_EUROPEAN]: true,
  [Cuisine.ETHIOPIAN]: true,
  [Cuisine.FILIPINO]: true,
  [Cuisine.FRENCH]: true,
  [Cuisine.FUSION]: true,
  [Cuisine.GERMAN]: true,
  [Cuisine.GREEK]: true,
  [Cuisine.INDIAN]: true,
  [Cuisine.INDONESIAN]: true,
  [Cuisine.IRISH]: true,
  [Cuisine.ITALIAN]: true,
  [Cuisine.JAPANESE]: true,
  [Cuisine.KOREAN]: true,
  [Cuisine.MALAYSIAN]: true,
  [Cuisine.MEDITERRANEAN]: true,
  [Cuisine.MEXICAN]: true,
  [Cuisine.MIDDLE_EASTERN]: true,
  [Cuisine.NORTH_AFRICAN]: true,
  [Cuisine.PAKISTANI]: true,
  [Cuisine.PERSIAN]: true,
  [Cuisine.PERUVIAN]: true,
  [Cuisine.PORTUGUESE]: true,
  [Cuisine.SCANDINAVIAN]: true,
  [Cuisine.SPANISH]: true,
  [Cuisine.SWISS]: true,
  [Cuisine.THAI]: true,
  [Cuisine.TURKISH]: true,
  [Cuisine.VIETNAMESE]: true,
  [Cuisine.WEST_AFRICAN]: true,
  [Cuisine.OTHER]: true,
});

export const toApiLanguage = (i18nLanguage?: string): Language =>
  i18nLanguage?.startsWith("en") ? Language.EN : Language.DE;

export const toFormValues = (dto: UserUiDTO): PreferencesFormValues => ({
  diet: dto.diet,
  cookingSkill: dto.cookingSkill,
  preferredCuisines: dto.preferredCuisines,
  allergens: dto.allergens,
  dislikedIngredientIds: dto.dislikedIngredients.flatMap((i) =>
    i.id === undefined ? [] : [i.id],
  ),
});

export const toUserUiDTO = (
  values: PreferencesFormValues,
  standingDataIngredients: IngredientUiDTO[],
): UserUiDTO => {
  const { dislikedIngredientIds, ...preferences } = values;
  return {
    ...preferences,
    preferredCuisines: preferences.preferredCuisines ?? [],
    allergens: preferences.allergens ?? [],
    dislikedIngredients: standingDataIngredients.filter(
      (ingredient) =>
        ingredient.id !== undefined &&
        (dislikedIngredientIds ?? []).includes(ingredient.id),
    ),
  };
};
