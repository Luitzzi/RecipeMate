import {
  ClockCircleOutlined,
  DashboardOutlined,
  GlobalOutlined,
  HeartOutlined,
} from "@ant-design/icons";
import { Card, Col, Flex, Row, Tag, Typography } from "antd";
import type { ReactNode } from "react";
import type { RecipeUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import {
  dietLabels,
  formatMinutes,
  getDifficultyLabel,
} from "../recipeLabels.ts";

type FactCardProps = {
  icon: ReactNode;
  label: string;
  value: string;
};

const FactCard = ({ icon, label, value }: FactCardProps) => (
  <Card size="small" style={{ height: "100%" }}>
    <Flex vertical align="center" style={{ textAlign: "center" }}>
      <span
        style={{ color: tokens.color.primary, fontSize: tokens.fontSize.large }}
      >
        {icon}
      </span>
      <Typography.Text type="secondary">{label}</Typography.Text>
      <Typography.Text strong>{value}</Typography.Text>
    </Flex>
  </Card>
);

type RecipeFactsProps = {
  recipe: RecipeUiDTO;
};

/** Cuisine, diet and difficulty of a recipe followed by its tags and cook time. */
export const RecipeFacts = ({ recipe }: RecipeFactsProps) => {
  const cuisines =
    recipe.cuisines.map((cuisine) => cuisine.name).join(", ") || "–";

  return (
    <Flex vertical gap={tokens.gap.medium}>
      <Row gutter={tokens.gap.small}>
        <Col span={8}>
          <FactCard icon={<GlobalOutlined />} label="Küche" value={cuisines} />
        </Col>
        <Col span={8}>
          <FactCard
            icon={<HeartOutlined />}
            label="Ernährung"
            value={dietLabels[recipe.diet]}
          />
        </Col>
        <Col span={8}>
          <FactCard
            icon={<DashboardOutlined />}
            label="Schwierigkeit"
            value={getDifficultyLabel(recipe.difficulty)}
          />
        </Col>
      </Row>
      <Flex align="center" justify="space-between" gap={tokens.gap.small}>
        <Flex wrap gap={tokens.gap.small}>
          {recipe.properties.map((property) => (
            <Tag key={property.id} color="blue" style={{ margin: 0 }}>
              {property.name}
            </Tag>
          ))}
        </Flex>
        <Typography.Text strong style={{ flexShrink: 0 }}>
          <ClockCircleOutlined
            style={{
              color: tokens.color.primary,
              marginRight: tokens.margin.small,
            }}
          />
          {formatMinutes(recipe.cookTimeInMinutes)}
        </Typography.Text>
      </Flex>
    </Flex>
  );
};
