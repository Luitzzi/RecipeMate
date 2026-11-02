import { useEffect, useState } from "react";

export const useSessionStorage = <T>(key: string, initialValue: T) => {
  const [value, setValue] = useState(() => {
    if (typeof window === "undefined") return initialValue;
    try {
      const stored = sessionStorage.getItem(key);
      return stored !== null ? (JSON.parse(stored) as T) : initialValue;
    } catch {
      return initialValue;
    }
  });

  useEffect(() => {
    try {
      sessionStorage.setItem(key, JSON.stringify(value));
    } catch {
      console.warn(`Setting session storage failed for key ${key}`);
    }
  }, [key, value]);

  return [value, setValue] as const;
};
