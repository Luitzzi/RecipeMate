import { Flex } from "antd";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { RecipeCreationForm } from "#/page/recipe_creation/components/RecipeCreationForm.tsx";

export const RecipeCreationPage = () => {
  return (
    <Flex vertical style={{ minHeight: "100%" }}>
      <SectionHeader title={"Rezepte Erstellung"} />
      <RecipeCreationForm />
    </Flex>
  );
};
