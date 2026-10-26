import {
  CounterUiControllerClient,
  type HttpClient,
} from "#/generated/interface-api.ts";

export function createApi(httpClient: HttpClient) {
  return {
    counter: new CounterUiControllerClient(httpClient),
    // user: new UserUiControllerClient(httpClient),
  };
}

export type Api = ReturnType<typeof createApi>;
