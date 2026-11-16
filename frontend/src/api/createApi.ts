import {
  ChatUiControllerClient,
  type HttpClient,
  ImageUiControllerClient,
  IngredientUiControllerClient,
  RecipeUiControllerClient,
  UserUiControllerClient,
  type HttpClient,
} from "#/generated/interface-api.ts";

export const createApi = (httpClient: HttpClient) => ({
  chat: new ChatUiControllerClient(httpClient),
  image: new ImageUiControllerClient(httpClient),
  ingredient: new IngredientUiControllerClient(httpClient),
  recipe: new RecipeUiControllerClient(httpClient),
  user: new UserUiControllerClient(httpClient),
});

export type Api = ReturnType<typeof createApi>;
