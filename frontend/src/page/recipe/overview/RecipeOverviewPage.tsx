import { FireOutlined } from "@ant-design/icons";
import { Button, Flex, Result, Skeleton, Tooltip, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";

import { useApi } from "#/api/apiContext.ts";
import {
  SectionHeader,
  TitleDivider,
} from "#/common/components/SectionHeader.tsx";
import { useFetch } from "#/common/hooks/useFetch.ts";
import { tokens } from "#/theme/tokens.ts";

import { IngredientList } from "./IngredientList.tsx";
import { RecipeFacts } from "./RecipeFacts.tsx";
import { RecipeImageCarousel } from "./RecipeImageCarousel.tsx";
import { StepList } from "./StepList.tsx";

export const RecipeOverviewPage = () => {
  const { id } = useParams();
  const { t } = useTranslation("recipeOverview");

  const api = useApi();

  const [recipe, loading, error] = useFetch(
    () => api.recipe.get(Number(id)),
    [id],
  );

  if (error) {
    return (
      <Result
        status="404"
        title={t("notFound.title")}
        subTitle={t("notFound.subTitle")}
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
      <SectionHeader title={t("title")} />

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          gridGap: "12px",
        }}
      >
        <h1 style={{ fontSize: "20px", fontWeight: "500" }}>{recipe.title}</h1>

        <Tooltip title={t("cookComingSoon")}>
          <Button type="primary" icon={<FireOutlined />} disabled>
            {t("cook")}
          </Button>
        </Tooltip>
      </div>

      <RecipeImageCarousel imageKeys={recipe.imageKeys} title={recipe.title} />
      <RecipeFacts recipe={recipe} />

      <Flex vertical gap={tokens.gap.medium}>
        <TitleDivider title={t("sections.ingredients")} />
        <IngredientList ingredients={recipe.ingredients} />
      </Flex>

      <Flex vertical gap={tokens.gap.medium}>
        <TitleDivider title={t("sections.steps")} />
        <StepList steps={recipe.steps} />
      </Flex>

      {recipe.note && (
        <Flex vertical gap={tokens.gap.medium}>
          <TitleDivider title={t("sections.note")} />
          <Typography.Paragraph style={{ margin: 0, whiteSpace: "pre-line" }}>
            {recipe.note}
          </Typography.Paragraph>
        </Flex>
      )}

      {recipe.sourceUrl && (
        <Typography.Text type="secondary">
          {t("source")}{" "}
          <Typography.Link href={recipe.sourceUrl} target="_blank">
            {recipe.sourceUrl}
          </Typography.Link>
        </Typography.Text>
      )}
    </Flex>
  );
};
