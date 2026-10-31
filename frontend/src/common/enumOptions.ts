import { toSelectOptions } from "#/common/components/input/SelectOption.ts";
import {
  Cuisine,
  Diet,
  HeatLevel,
  OvenMode,
  RecipeProperty,
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

const DIET_LABELS: Record<Diet, string> = {
  [Diet.VEGAN]: "Vegan",
  [Diet.VEGETARIAN]: "Vegetarian",
  [Diet.PESCATARIAN]: "Pescatarian",
  [Diet.OMNIVORE]: "Omnivore",
};

export const DIET_OPTIONS = toSelectOptions(
  Object.keys(DIET_LABELS) as Diet[],
  (diet) => diet,
  (diet) => DIET_LABELS[diet],
);

const CUISINE_LABELS: Record<Cuisine, string> = {
  [Cuisine.AMERICAN]: "American",
  [Cuisine.ARGENTINIAN]: "Argentinian",
  [Cuisine.AUSTRIAN]: "Austrian",
  [Cuisine.BRAZILIAN]: "Brazilian",
  [Cuisine.BRITISH]: "British",
  [Cuisine.CARIBBEAN]: "Caribbean",
  [Cuisine.CHINESE]: "Chinese",
  [Cuisine.EASTERN_EUROPEAN]: "Eastern European",
  [Cuisine.ETHIOPIAN]: "Ethiopian",
  [Cuisine.FILIPINO]: "Filipino",
  [Cuisine.FRENCH]: "French",
  [Cuisine.FUSION]: "Fusion",
  [Cuisine.GERMAN]: "German",
  [Cuisine.GREEK]: "Greek",
  [Cuisine.INDIAN]: "Indian",
  [Cuisine.INDONESIAN]: "Indonesian",
  [Cuisine.IRISH]: "Irish",
  [Cuisine.ITALIAN]: "Italian",
  [Cuisine.JAPANESE]: "Japanese",
  [Cuisine.KOREAN]: "Korean",
  [Cuisine.MALAYSIAN]: "Malaysian",
  [Cuisine.MEDITERRANEAN]: "Mediterranean",
  [Cuisine.MEXICAN]: "Mexican",
  [Cuisine.MIDDLE_EASTERN]: "Middle Eastern",
  [Cuisine.NORTH_AFRICAN]: "North African",
  [Cuisine.PAKISTANI]: "Pakistani",
  [Cuisine.PERSIAN]: "Persian",
  [Cuisine.PERUVIAN]: "Peruvian",
  [Cuisine.PORTUGUESE]: "Portuguese",
  [Cuisine.SCANDINAVIAN]: "Scandinavian",
  [Cuisine.SPANISH]: "Spanish",
  [Cuisine.SWISS]: "Swiss",
  [Cuisine.THAI]: "Thai",
  [Cuisine.TURKISH]: "Turkish",
  [Cuisine.VIETNAMESE]: "Vietnamese",
  [Cuisine.WEST_AFRICAN]: "West African",
  [Cuisine.OTHER]: "Other",
};

export const CUISINE_OPTIONS = toSelectOptions(
  Object.keys(CUISINE_LABELS) as Cuisine[],
  (cuisine) => cuisine,
  (cuisine) => CUISINE_LABELS[cuisine],
);

const RECIPE_PROPERTY_LABELS: Record<RecipeProperty, string> = {
  [RecipeProperty.BREAKFAST]: "Breakfast",
  [RecipeProperty.BRUNCH]: "Brunch",
  [RecipeProperty.LUNCH]: "Lunch",
  [RecipeProperty.DINNER]: "Dinner",
  [RecipeProperty.SNACK]: "Snack",
  [RecipeProperty.APPETIZER]: "Appetizer",
  [RecipeProperty.MAIN_COURSE]: "Main course",
  [RecipeProperty.SIDE_DISH]: "Side dish",
  [RecipeProperty.SOUP]: "Soup",
  [RecipeProperty.SALAD]: "Salad",
  [RecipeProperty.DESSERT]: "Dessert",
  [RecipeProperty.BAKED_GOODS]: "Baked goods",
  [RecipeProperty.DRINK]: "Drink",
  [RecipeProperty.SAUCE]: "Sauce",
  [RecipeProperty.DIP]: "Dip",
  [RecipeProperty.QUICK]: "Quick",
  [RecipeProperty.HEALTHY]: "Healthy",
  [RecipeProperty.BUDGET]: "Budget",
  [RecipeProperty.MEAL_PREP]: "Meal prep",
  [RecipeProperty.ONE_POT]: "One pot",
  [RecipeProperty.COMFORT_FOOD]: "Comfort food",
  [RecipeProperty.SPICY]: "Spicy",
  [RecipeProperty.KID_FRIENDLY]: "Kid friendly",
  [RecipeProperty.LOW_CARB]: "Low carb",
  [RecipeProperty.HIGH_PROTEIN]: "High protein",
  [RecipeProperty.GLUTEN_FREE]: "Gluten free",
  [RecipeProperty.LACTOSE_FREE]: "Lactose free",
  [RecipeProperty.PARTY]: "Party",
  [RecipeProperty.BARBECUE]: "Barbecue",
  [RecipeProperty.CHRISTMAS]: "Christmas",
};

export const RECIPE_PROPERTY_OPTIONS = toSelectOptions(
  Object.keys(RECIPE_PROPERTY_LABELS) as RecipeProperty[],
  (property) => property,
  (property) => RECIPE_PROPERTY_LABELS[property],
);
