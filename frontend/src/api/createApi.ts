import {
  ChatUiControllerClient,
  FolderUiControllerClient,
  type HttpClient,
  ImageUiControllerClient,
  IngredientUiControllerClient,
  RecipeUiControllerClient,
} from "#/generated/interface-api.ts";

export const createApi = (httpClient: HttpClient) => ({
  chat: new ChatUiControllerClient(httpClient),
  folder: new FolderUiControllerClient(httpClient),
  image: new ImageUiControllerClient(httpClient),
  ingredient: new IngredientUiControllerClient(httpClient),
  recipe: new RecipeUiControllerClient(httpClient),
});

export type Api = ReturnType<typeof createApi>;
