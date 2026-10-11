import { type DependencyList, useEffect, useState } from "react";

export type FetchResult<T> = {
  result: T | undefined;
  loading: boolean;
  error: Error | undefined;
};

/**
 * Fetch data when dependencies change
 * @param fetch Api request
 * @param dependencies Dependencies triggering a refetch
 */
export const useFetch = <T>(
  fetch: () => Promise<T>,
  dependencies: DependencyList,
): [T | undefined, boolean, Error | undefined] => {
  const [result, setResult] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | undefined>(undefined);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLoading(true);
    fetch()
      .then((result) => setResult(result))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
    // eslint-disable-next-line
  }, dependencies);

  return [result, loading, error];
};
