import { useCallback, useRef, useState } from "react";

import { useApi } from "#/api/apiContext.ts";
import type {
  FeedSectionUiDTO,
  RecipeSummaryUiDTO,
} from "#/generated/interface-api.ts";

/**
 * Recipes of a feed section, starting with the first page delivered by the feed.
 * `loadNext` appends the next page; it is ignored while loading or when everything is loaded.
 */
export const useFeedSectionPages = (section: FeedSectionUiDTO) => {
  const api = useApi();

  const { recipes: firstPage } = section;

  const [recipes, setRecipes] = useState<RecipeSummaryUiDTO[]>(
    firstPage.content,
  );

  const [nextPage, setNextPage] = useState(firstPage.page + 1);
  const [totalPages, setTotalPages] = useState(firstPage.totalPages);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error>();

  // Guards against parallel requests before the loading state is rendered
  const loadingRef = useRef(false);

  const hasMore = nextPage < totalPages;

  const loadNext = useCallback(async () => {
    if (loadingRef.current || !hasMore) return;

    loadingRef.current = true;
    setLoading(true);
    setError(undefined);

    try {
      const page = await api.feed.getSectionPage({
        type: section.type,
        property: section.property,
        page: nextPage,
        size: firstPage.size,
      });

      // Recipes created meanwhile shift the pages: skip recipes that are already shown
      setRecipes((loaded) => {
        const loadedIds = new Set(loaded.map((recipe) => recipe.id));
        return [
          ...loaded,
          ...page.content.filter((recipe) => !loadedIds.has(recipe.id)),
        ];
      });

      setNextPage(page.page + 1);
      setTotalPages(page.totalPages);
    } catch (err) {
      setError(err as Error);
    } finally {
      loadingRef.current = false;
      setLoading(false);
    }
  }, [api, section.type, section.property, firstPage.size, nextPage, hasMore]);

  return { recipes, loading, error, hasMore, loadNext };
};
