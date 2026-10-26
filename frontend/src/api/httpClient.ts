import type { HttpClient } from "../generated/interface-api";

type QueryParams = Record<string, unknown> | undefined;

function buildQueryString(queryParams: QueryParams): string {
  const definedParams = Object.entries(queryParams ?? {})
    .filter(([, value]) => value != null)
    .map(([key, value]) => [key, String(value)]);

  const encoded = new URLSearchParams(definedParams).toString();
  return encoded ? `?${encoded}` : "";
}

function jsonBody(data: unknown): RequestInit {
  if (data === undefined) return {};

  return {
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  };
}

export const fetchHttpClient: HttpClient = {
  async request({ method, url, queryParams, data }) {
    const requestUrl = `/${url}${buildQueryString(queryParams)}`;
    const response = await fetch(requestUrl, {
      method,
      ...jsonBody(data),
    });

    if (!response.ok) {
      throw new Error(`${method} ${url} failed with status ${response.status}`);
    }

    const responseText = await response.text();
    return responseText ? JSON.parse(responseText) : undefined;
  },
};
