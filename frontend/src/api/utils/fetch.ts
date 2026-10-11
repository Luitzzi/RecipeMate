export const getApiFetchErrorMessage = (
  entityName: string,
  message: string,
) => {
  return `Could not load ${entityName}: ${message}`;
};

const request = async <T>(url: string, init: RequestInit = {}) => {
  const response = await fetch(url, {
    ...init,
    headers: { "Content-Type": "application/json", ...init.headers },
  });
  if (!response.ok) {
    throw new Error(`${init.method} ${url} failed with ${response.status}`);
  }
  return (await response.json()) as T;
};

export const get = <T>(url: string) => {
  return request<T>(url, { method: "GET" });
};

export const post = <T, BodyType = unknown>(url: string, body: BodyType) => {
  return request<T>(url, {
    method: "POST",
    body: JSON.stringify(body),
  });
};

export const put = <T, BodyType = unknown>(url: string, body: BodyType) => {
  return request<T>(url, {
    method: "PUT",
    body: JSON.stringify(body),
  });
};

export const remove = <T>(url: string) => {
  return request<T>(url, {
    method: "DELETE",
  });
};
