import type { HttpClient } from "#/generated/interface-api.ts";

type QueryParams = Record<string, unknown> | undefined;

const buildQueryString = (queryParams: QueryParams): string => {
  const definedParams = Object.entries(queryParams ?? {})
    .filter(([, value]) => value != null)
    .map(([key, value]) => [key, String(value)]);

  const encoded = new URLSearchParams(definedParams).toString();
  return encoded ? `?${encoded}` : "";
};

const jsonBody = (data: unknown): RequestInit => {
  if (data === undefined) return {};

  return {
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  };
};

const getMediaType = (response: Response): string => {
  // Strip endings like "; charset=UTF-8".
  return response.headers.get("Content-Type")?.split(";")[0] ?? "";
};

const RESPONSE_PARSERS: Partial<Record<string, (text: string) => unknown>> = {
  "application/json": JSON.parse,
};

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
    if (!responseText) return undefined as never;

    const parse = RESPONSE_PARSERS[getMediaType(response)];
    if (parse) return parse(responseText) as never;

    // Return unknown media types as plain text
    return responseText as never;
  },
};
