import type { ReactNode } from "react";

import type { HttpClient } from "#/generated/interface-api.ts";

import { ApiContext } from "./apiContext";
import { createApi } from "./createApi";

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
