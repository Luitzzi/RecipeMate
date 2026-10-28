import type { ReactNode } from "react";
import { ApiContext } from "./apiContext";
import { createApi } from "./createApi";
import type { HttpClient } from "#/generated/interface-api.ts";

export const ApiProvider = ({
  httpClient,
  children,
}: {
  httpClient: HttpClient;
  children: ReactNode;
}) => {
  const api = createApi(httpClient);
  return <ApiContext.Provider value={api}>{children}</ApiContext.Provider>;
};
