import { useFetch } from "./useFetch.ts";

export const useFetchOnce = <T>(fetch: () => Promise<T>) => {
  return useFetch(fetch, []);
};
