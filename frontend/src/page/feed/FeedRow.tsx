import { Alert, Button, Flex, Spin } from "antd";
import { useCallback, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";

import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import {
  FeedSectionType,
  type FeedSectionUiDTO,
} from "#/generated/interface-api.ts";
import { FeedRecipeCard } from "#/page/feed/FeedRecipeCard.tsx";
import { useFeedSectionPages } from "#/page/feed/useFeedSectionPages.ts";
import { tokens } from "#/theme/tokens.ts";

/** Distance to the end of the row at which the next page is loaded. */
const LOAD_MORE_THRESHOLD_PX = 400;

type FeedRowProps = {
  section: FeedSectionUiDTO;
};

/**
 * Horizontal row of the recipes of one feed section.
 * Starts with the first page from the feed and loads the next page
 * when the row is scrolled close to its end.
 */
export const FeedRow = ({ section }: FeedRowProps) => {
  const { t } = useTranslation("feed");
  const { t: tEnums } = useTranslation("enums");

  const { recipes, loading, error, hasMore, loadNext } =
    useFeedSectionPages(section);

  const scrollRef = useRef<HTMLDivElement>(null);

  const canLoad = hasMore && !loading && !error;

  // Reads the scroll position directly from the DOM, so it is never stale after a page was added
  const loadMoreIfNearEnd = useCallback(() => {
    const row = scrollRef.current;
    if (!row || !canLoad) return;
    const distanceToEnd = row.scrollWidth - row.scrollLeft - row.clientWidth;
    if (distanceToEnd <= LOAD_MORE_THRESHOLD_PX) void loadNext();
  }, [canLoad, loadNext]);

  useEffect(() => {
    loadMoreIfNearEnd();
  }, [recipes.length, loadMoreIfNearEnd]);

  const title =
    section.type === FeedSectionType.RECENT || !section.property
      ? t("sections.recent")
      : tEnums(`recipeProperty.${section.property}`);

  return (
    <div>
      <SectionHeader
        title={title}
        description={t("recipeCount", {
          count: section.recipes.totalElements,
        })}
      />

      {error && (
        <Alert
          type="error"
          title={t("loadMoreError")}
          action={
            <Button size="small" onClick={() => void loadNext()}>
              {t("retry")}
            </Button>
          }
          style={{ marginBottom: tokens.margin.medium }}
        />
      )}

      <Flex
        ref={scrollRef}
        onScroll={loadMoreIfNearEnd}
        style={{ overflowX: "auto" }}
        gap={tokens.gap.large}
      >
        {recipes.map((recipe) => (
          <FeedRecipeCard key={recipe.id} recipe={recipe} />
        ))}

        {loading && (
          <Flex align="center" justify="center">
            <Spin />
          </Flex>
        )}
      </Flex>
    </div>
  );
};
