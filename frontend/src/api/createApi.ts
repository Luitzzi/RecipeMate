import {
  ChatUiControllerClient,
  ImageUiControllerClient,
  IngredientUiControllerClient,
  MealPrepUiControllerClient,
  RecipeUiControllerClient,
  type HttpClient,
} from "#/generated/interface-api.ts";

export const createApi = (httpClient: HttpClient) => ({
  chat: new ChatUiControllerClient(httpClient),
  image: new ImageUiControllerClient(httpClient),
  ingredient: new IngredientUiControllerClient(httpClient),
  mealPrepping: new MealPrepUiControllerClient(httpClient),
  recipe: new RecipeUiControllerClient(httpClient),
});

export type Api = ReturnType<typeof createApi>;
