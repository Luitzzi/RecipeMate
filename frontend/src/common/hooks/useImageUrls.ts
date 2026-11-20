import { useRef, useState } from "react";

import { useApi } from "#/api/apiContext.ts";

const CACHE_VALIDITY_MS = 10 * 60 * 1000;

/**
 * Returns a getter that resolves an image key to its download url.
 * Each key is cached for CACHE_VALIDITY_MS (10 minutes) and requested again afterwards.
 * Returns undefined until the url is loaded or if loading fails.
 *
 * Note: The request is started during render, so expired urls are only
 * refreshed when the component re-renders.
 */
export const useImageUrls = () => {
  const api = useApi();
  const [urls, setUrls] = useState<Record<string, string>>({});
  const requestedAt = useRef(new Map<string, number>());

  return (imageKey?: string): string | undefined => {
    if (!imageKey) return undefined;
    const last = requestedAt.current.get(imageKey);
    if (!last || Date.now() - last > CACHE_VALIDITY_MS) {
      requestedAt.current.set(imageKey, Date.now());
      api.image
        .getDownloadUrl({ key: imageKey })
        .then(({ url }) => setUrls((prev) => ({ ...prev, [imageKey]: url })))
        .catch(() => undefined);
    }
    return urls[imageKey];
  };
};
