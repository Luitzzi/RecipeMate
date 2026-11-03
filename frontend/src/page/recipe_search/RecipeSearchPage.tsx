import { Form, Pagination } from "antd";
import { useEffect, useState } from "react";

import { useApi } from "#/api/apiContext.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import type { RecipeFilterUiDTO } from "#/generated/interface-api.ts";
import { RecipeSearchBox } from "#/page/recipe_search/components/RecipeSearchBox.tsx";
import { RecipeSearchResults } from "#/page/recipe_search/components/RecipeSearchResults.tsx";
import { toRecipeFilterUiDTO } from "#/page/recipe_search/utils/recipeFilterMapper.ts";
import type { RecipeSearchFormValues } from "#/page/recipe_search/utils/recipeSearchFormUtils.ts";
import { tokens } from "#/theme/tokens.ts";

const RESULTS_PER_PAGE = 20;

export const RecipeSearchPage = () => {
  const { recipe } = useApi();
  const notify = useNotification();
  const [form] = Form.useForm<RecipeSearchFormValues>();

  const [folderName, setFolderName] = useState<string>();
  // Changing the page must keep the searched filter, even if the form was edited since
  const [searchedFilter, setSearchedFilter] = useState<RecipeFilterUiDTO>();

  const [searchRecipes, resultPage, searching, searchError] = useMutation(
    (filter: RecipeFilterUiDTO, page: number) =>
      recipe.filter(filter, { page, size: RESULTS_PER_PAGE }),
  );
  useEffect(() => {
    if (searchError !== undefined) {
      notify.error({ title: "Die Suche ist fehlgeschlagen" });
    }
  }, [searchError, notify]);

  const getResultsPrefix = () => {
    if (folderName !== undefined) {
      return ` für Folder ${folderName}`;
    }
    return "";
  };

  const handleSearch = (values: RecipeSearchFormValues) => {
    const filter = toRecipeFilterUiDTO(values);
    setFolderName(filter.folderName);
    setSearchedFilter(filter);
    void searchRecipes(filter, 0);
  };

  return (
    <>
      <RecipeSearchBox
        form={form}
        onSearch={handleSearch}
        searching={searching}
      />

      <SectionHeader
        title={"Ergebnisse" + getResultsPrefix()}
        description={
          resultPage && !searchError
            ? `${resultPage.totalElements} Rezepte gefunden`
            : undefined
        }
      />
      {resultPage && !searchError && (
        <>
          <RecipeSearchResults recipes={resultPage.content} />
          <Pagination
            align={"center"}
            current={resultPage.page + 1}
            pageSize={resultPage.size}
            total={resultPage.totalElements}
            showSizeChanger={false}
            hideOnSinglePage
            disabled={searching}
            onChange={(page) => {
              if (searchedFilter) void searchRecipes(searchedFilter, page - 1);
            }}
            style={{ marginTop: tokens.margin.medium }}
          />
        </>
      )}
    </>
  );
};
