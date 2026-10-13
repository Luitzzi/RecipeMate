import { useCallback, useState } from "react";

export const useMutation = <TArgs extends unknown[], T>(
  mutation: (...args: TArgs) => Promise<T>,
): [
  (...args: TArgs) => Promise<T | undefined>,
  T | undefined,
  boolean,
  Error | undefined,
] => {
  const [result, setResult] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | undefined>(undefined);

  const execute = useCallback(
    async (...args: TArgs) => {
      setLoading(true);
      setError(undefined);
      try {
        const response = await mutation(...args);
        setResult(response);
        return response;
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    },
    [mutation],
  );

  return [execute, result, loading, error];
};
