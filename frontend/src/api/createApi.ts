import {
  ImageUiControllerClient,
  RecipeUiControllerClient,
  type HttpClient,
} from "#/generated/interface-api.ts";

export function createApi(httpClient: HttpClient) {
  return {
    recipe: new RecipeUiControllerClient(httpClient),
    image: new ImageUiControllerClient(httpClient),
    // user: new UserUiControllerClient(httpClient),
  };
}

export type Api = ReturnType<typeof createApi>;
