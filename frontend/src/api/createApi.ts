import {  type HttpClient } from "#/generated/interface-api.ts";

export function createApi(httpClient: HttpClient) {
  return {
    // user: new UserUiControllerClient(httpClient),
  };
}

export type Api = ReturnType<typeof createApi>;