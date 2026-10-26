import { Card, Flex, Typography } from "antd";
import type { RecipeStepUiDTOUnion } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import {
  formatMinutes,
  heatLevelLabels,
  ovenModeLabels,
  stepMethodLabels,
} from "../recipeLabels.ts";

/** Additional information depending on the step method, e.g. the oven temperature. */
const getStepDetails = (step: RecipeStepUiDTOUnion): string | undefined => {
  switch (step.stepMethod) {
    case "PREPARE":
      return undefined;
    case "STOVE":
      return heatLevelLabels[step.heatLevel];
    case "OVEN":
      return [
        `${step.temperatureC} °C`,
        ovenModeLabels[step.ovenMode],
        formatMinutes(Math.round(step.durationSeconds / 60)),
        step.preheat ? "vorheizen" : undefined,
      ]
        .filter(Boolean)
        .join(" · ");
  }
};

type StepListProps = {
  steps: RecipeStepUiDTOUnion[];
};

export const StepList = ({ steps }: StepListProps) => {
  if (steps.length === 0) {
    return (
      <Typography.Text type="secondary">Keine Arbeitsschritte</Typography.Text>
    );
  }

  return (
    <Flex vertical gap={tokens.gap.medium}>
      {steps.map((step, index) => (
        <Card
          key={step.id ?? index}
          size="small"
          title={`${index + 1}. ${stepMethodLabels[step.stepMethod]}`}
          extra={
            <Typography.Text type="secondary">
              {getStepDetails(step)}
            </Typography.Text>
          }
        >
          <Typography.Paragraph style={{ margin: 0, whiteSpace: "pre-line" }}>
            {step.description}
          </Typography.Paragraph>
        </Card>
      ))}
    </Flex>
  );
};
