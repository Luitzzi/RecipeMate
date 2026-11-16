import { Card, Flex, Typography } from "antd";
import type { TFunction } from "i18next";
import { useTranslation } from "react-i18next";

import type { RecipeStepDTOUnion } from "#/generated/interface-api.ts";
import { useRecipeLabels } from "#/page/recipe/recipeLabels.ts";
import { tokens } from "#/theme/tokens.ts";

/** Additional information depending on the step method, e.g. the oven temperature. */
const getStepDetails = (
  step: RecipeStepDTOUnion,
  t: TFunction<"enums">,
  formatMinutes: (minutes: number) => string,
  preheatLabel: string,
): string | undefined => {
  switch (step.stepMethod) {
    case "PREPARE":
      return undefined;
    case "STOVE":
      return t(`heatLevel.${step.heatLevel}`);
    case "OVEN":
      return [
        `${step.temperatureC} °C`,
        t(`ovenMode.${step.ovenMode}`),
        step.durationSeconds == null
          ? undefined
          : formatMinutes(Math.round(step.durationSeconds / 60)),
        step.preheat ? preheatLabel : undefined,
      ]
        .filter(Boolean)
        .join(" · ");
  }
};

type StepListProps = {
  steps: RecipeStepDTOUnion[];
};

export const StepList = ({ steps }: StepListProps) => {
  const { t, formatMinutes } = useRecipeLabels();
  const { t: tOverview } = useTranslation("recipeOverview");

  if (steps.length === 0) {
    return (
      <Typography.Text type="secondary">{tOverview("noSteps")}</Typography.Text>
    );
  }

  return (
    <Flex vertical gap={tokens.gap.medium}>
      {steps.map((step, index) => (
        <Card
          key={index}
          size="small"
          title={`${index + 1}. ${t(`stepMethod.${step.stepMethod}`)}`}
          extra={
            <Typography.Text type="secondary">
              {getStepDetails(step, t, formatMinutes, tOverview("preheat"))}
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
