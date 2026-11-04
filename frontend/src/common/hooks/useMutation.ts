import { useCallback, useState } from "react";

export type MutationOptions = {
  /**
   * Called once per failed execution. Use this for side effects like notifications
   * instead of a useEffect on the returned error, which re-fires on re-renders and HMR.
   */
  onError?: (error: Error) => void;
};

export const useMutation = <TArgs extends unknown[], T>(
  mutation: (...args: TArgs) => Promise<T>,
  options?: MutationOptions,
): readonly [
  (...args: TArgs) => Promise<T | undefined>,
  T | undefined,
  boolean,
  Error | undefined,
] => {
  const [result, setResult] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | undefined>(undefined);
  const onError = options?.onError;

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
        onError?.(err as Error);
      } finally {
        setLoading(false);
      }
    },
    [mutation, onError],
  );

  return [execute, result, loading, error] as const;
};
