import { FireOutlined } from "@ant-design/icons";
import { Button, Flex, Result, Skeleton, Tooltip, Typography } from "antd";
import { useParams } from "react-router";
import {
  SectionHeader,
  TitleDivider,
} from "#/common/components/SectionHeader.tsx";
import { useFetch } from "#/common/hooks/useFetch.ts";
import { Language } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import { IngredientList } from "./IngredientList.tsx";
import { RecipeFacts } from "./RecipeFacts.tsx";
import { RecipeImageCarousel } from "./RecipeImageCarousel.tsx";
import { StepList } from "./StepList.tsx";
import {useApi} from "#/api/apiContext.ts";

export const RecipeOverviewPage = () => {
  const { recipeId } = useParams();

  const api = useApi();

  const [recipe, loading, error] = useFetch(() =>
      api.recipe.get(Number(recipeId), { language: Language.DE }), [recipeId]);

  console.log(recipe);

  if (error) {
    return (
      <Result
        status="404"
        title="Rezept nicht gefunden"
        subTitle="Das Rezept existiert nicht oder konnte nicht geladen werden."
      />
    );
  }

  if (loading || !recipe) {
    return <Skeleton active paragraph={{ rows: 8 }} />;
  }

  return (
    <Flex
      vertical
      gap={tokens.gap.large}
      style={{ paddingBottom: tokens.padding.large }}
    >
      <SectionHeader title="Rezepte Übersicht" />

      <div style={{ display: "flex", justifyContent: "space-between", gridGap: '12px' }}>
        <h1 style={{ fontSize: '20px', fontWeight: '500' }}>{recipe.title}</h1>

        <Tooltip title="Der Kochmodus folgt bald">
          <Button type="primary" icon={<FireOutlined />} disabled>
            Kochen
          </Button>
        </Tooltip>
      </div>

      <RecipeImageCarousel imageKeys={recipe.imageKeys} title={recipe.title} />
      <RecipeFacts recipe={recipe} />

      <Flex vertical gap={tokens.gap.medium}>
        <TitleDivider title="Zutaten" />
        <IngredientList ingredients={recipe.ingredients} />
      </Flex>

      <Flex vertical gap={tokens.gap.medium}>
        <TitleDivider title="Arbeitsschritte" />
        <StepList steps={recipe.steps} />
      </Flex>

      {recipe.note && (
        <Flex vertical gap={tokens.gap.medium}>
          <TitleDivider title="Notiz" />
          <Typography.Paragraph style={{ margin: 0, whiteSpace: "pre-line" }}>
            {recipe.note}
          </Typography.Paragraph>
        </Flex>
      )}

      {recipe.sourceUrl && (
        <Typography.Text type="secondary">
          Quelle:{" "}
          <Typography.Link href={recipe.sourceUrl} target="_blank">
            {recipe.sourceUrl}
          </Typography.Link>
        </Typography.Text>
      )}
    </Flex>
  );
};
