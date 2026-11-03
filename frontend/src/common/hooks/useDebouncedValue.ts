import { useEffect, useState } from "react";

/**
 * Updates a values after a given delay.
 * It is important that the value triggers a rerender in the component.
 * Elsewise, it would only call useDebouncedValue once with its initial value
 * and then only when the component rerenders.
 *
 * Use-case: Text search inside a component.
 * The user should finish typing before for example the fetch is triggered.
 */
export const useDebouncedValue = <T>(value: T, delayMs: number) => {
  const [debounced, setDebounced] = useState<T>(value);
  useEffect(() => {
    const timeout = setTimeout(() => setDebounced(value), delayMs);
    return () => clearTimeout(timeout);
  }, [value, delayMs]);
  return debounced;
};
