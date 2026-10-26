import { Flex, Typography } from "antd";
import type { RecipeIngredientUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import { formatAmount } from "../recipeLabels.ts";

type IngredientListProps = {
  ingredients: RecipeIngredientUiDTO[];
};

export const IngredientList = ({ ingredients }: IngredientListProps) => {
  if (ingredients.length === 0) {
    return <Typography.Text type="secondary">Keine Zutaten</Typography.Text>;
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
