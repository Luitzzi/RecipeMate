import { useState } from "react";
import { useEffectOnce } from "./useEffectOnce.ts";

export const useFetchOnce = <T>(
  fetch: () => Promise<T>,
): [T | undefined, boolean, Error | undefined] => {
  const [result, setResult] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | undefined>(undefined);

  useEffectOnce(() => {
    fetch()
      .then((result) => setResult(result))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  });
  return [result, loading, error];
};
