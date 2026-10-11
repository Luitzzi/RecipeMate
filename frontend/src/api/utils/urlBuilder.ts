import { config } from "../../assets/config.ts";

export const buildURL = (path: string, queryParameters?: string) => {
  let completedQueryParameters = "";
  if (queryParameters) {
    completedQueryParameters = `?${queryParameters}`;
  }
  return new URL(
    `${config.backendBaseUrl}/${path}${completedQueryParameters}`,
    window.location.origin,
  );
};
