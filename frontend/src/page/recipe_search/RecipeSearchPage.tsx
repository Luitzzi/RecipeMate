import { Form, Pagination } from "antd";
import { useState } from "react";

import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { RecipeSearchBox } from "#/page/recipe_search/components/RecipeSearchBox.tsx";
import type { RecipeSearchFormValues } from "#/page/recipe_search/utils/recipeSearchFormUtils.ts";

export const RecipeSearchPage = () => {
  const [form] = Form.useForm<RecipeSearchFormValues>();
  const [folderName, setFolderName] = useState<string>();

  const getResultsPrefix = () => {
    if (folderName !== undefined) {
      return ` für Folder ${folderName}`;
    }
    return "";
  };

  const handleSearch = (values: RecipeSearchFormValues) => {
    const folder = values.filterByFolder ? values.folderName?.trim() : "";
    setFolderName(folder || undefined);
    // TODO: Request the recipes as soon as the filter endpoint is available
  };

  return (
    <>
      <RecipeSearchBox form={form} onSearch={handleSearch} />
      <SectionHeader title={"Ergebnisse" + getResultsPrefix()} />
      <Pagination />
    </>
  );
};
