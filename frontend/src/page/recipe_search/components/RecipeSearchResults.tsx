import { Card, Col, Empty, Row, Typography } from "antd";

import type { RecipeUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";

export type RecipeSearchResultsProps = {
  recipes: RecipeUiDTO[];
};

export const RecipeSearchResults = ({ recipes }: RecipeSearchResultsProps) => {
  if (recipes.length === 0) {
    return (
      <Empty
        image={Empty.PRESENTED_IMAGE_SIMPLE}
        description={"Keine Rezepte gefunden"}
      />
    );
  }

  return (
    <Row gutter={[tokens.gap.small, tokens.gap.small]}>
      {recipes.map((recipe) => (
        <Col key={recipe.id} xs={12} md={8}>
          <Card size={"small"} title={recipe.title} style={{ height: "100%" }} onClick={() => window.location.assign(`/recipe/${recipe.id}`)}>
            <Typography.Paragraph
              type={"secondary"}
              ellipsis={{ rows: 2 }}
              style={{ marginBottom: tokens.margin.small }}
            >
              {recipe.description}
            </Typography.Paragraph>
            <Typography.Text>{recipe.cookTimeInMinutes} min</Typography.Text>
          </Card>
        </Col>
      ))}
    </Row>
  );
};
