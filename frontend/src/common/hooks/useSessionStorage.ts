import { type Dispatch, type SetStateAction, useEffect, useState } from "react";

/**
 * `useState` persisted in `sessionStorage`, so the value survives reloads within the tab.
 * Falls back to in-memory state if storage is unavailable.
 *
 * @param key - `sessionStorage` key. Cannot change between renders.
 * @param initialValue - Used if nothing is stored or the stored value cannot be read.
 * @returns `[value, setValue]`, like `useState`.
 */
export const useSessionStorage = <T>(
  key: string,
  initialValue: T,
): readonly [T, Dispatch<SetStateAction<T>>] => {
  // Freeze the key on mount: later changes are ignored.
  const [storageKey] = useState(key);

  // Read sessionStorage once on mount.
  const [value, setValue] = useState(() => {
    try {
      const stored = sessionStorage.getItem(storageKey);
      return stored !== null ? (JSON.parse(stored) as T) : initialValue;
    } catch {
      // Storage access denied or stored value is not valid JSON.
      return initialValue;
    }
  });

  // Write every change of the value back to sessionStorage.
  useEffect(() => {
    try {
      sessionStorage.setItem(storageKey, JSON.stringify(value));
    } catch {
      console.warn(`Setting session storage failed for key ${storageKey}`);
    }
  }, [storageKey, value]);

  return [value, setValue] as const;
};
