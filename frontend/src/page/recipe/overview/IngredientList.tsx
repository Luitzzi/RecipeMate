import { Flex, Typography } from "antd";
import { useTranslation } from "react-i18next";

import type { RecipeIngredientUiDTO } from "#/generated/interface-api.ts";
import { useRecipeLabels } from "#/page/recipe/recipeLabels.ts";
import { tokens } from "#/theme/tokens.ts";

type IngredientListProps = {
  ingredients: RecipeIngredientUiDTO[];
};

export const IngredientList = ({ ingredients }: IngredientListProps) => {
  const { formatAmount } = useRecipeLabels();
  const { t } = useTranslation("recipeOverview");

  if (ingredients.length === 0) {
    return (
      <Typography.Text type="secondary">{t("noIngredients")}</Typography.Text>
    );
  }

  return (
    <Flex vertical gap={tokens.gap.small}>
      {ingredients.map(({ ingredient, amount, unit }) => (
        <Flex
          key={ingredient.id ?? ingredient.name}
          align="center"
          justify="space-between"
          gap={tokens.gap.medium}
          style={{
            padding: `${tokens.padding.small}px ${tokens.padding.medium}px`,
            border: `${tokens.borderWidth}px solid ${tokens.color.border.default}`,
            borderRadius: tokens.radius.large,
          }}
        >
          <Typography.Text>{ingredient.name}</Typography.Text>
          <Typography.Text strong style={{ flexShrink: 0 }}>
            {formatAmount(amount, unit)}
          </Typography.Text>
        </Flex>
      ))}
    </Flex>
  );
};
