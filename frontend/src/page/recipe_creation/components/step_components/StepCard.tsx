import { type RecipeStepDTO, StepMethod } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import { Flex, Form, Input, Select } from "antd";
import { useState } from "react";
import { OvenStepInputs } from "#/page/recipe_creation/components/step_components/OvenStepInputs.tsx";
import { StoveStepInputs } from "#/page/recipe_creation/components/step_components/StoveStepInputs.tsx";
import { STEP_OPTIONS } from "#/common/enumOptions.ts";

export type StepCreationCardProps = {
  index: number;
};

export const StepCard = ({ index }: StepCreationCardProps) => {
  const [stepMethod, setStepMethod] = useState<StepMethod>();
  const isOvenStep = stepMethod === StepMethod.OVEN;
  const isStoveStep = stepMethod === StepMethod.STOVE;

  return (
    <Flex
      vertical
      gap={tokens.gap.small}
      style={{
        border: tokens.border,
        padding: tokens.padding.small,
      }}
    >
      <Flex gap={tokens.gap.small} align={"center"}>
        <Form.Item
          name={[index, "stepMethod" satisfies keyof RecipeStepDTO]}
          rules={[{ required: true, message: "Select a step method" }]}
          style={{ marginBottom: 0 }}
        >
          <Select
            size={"large"}
            placeholder={"Step method"}
            onChange={(newValue) => setStepMethod(newValue)}
            options={STEP_OPTIONS}
            style={{ width: tokens.width.selectField }}
          />
        </Form.Item>
        {isOvenStep && <OvenStepInputs index={index} isRequired={isOvenStep} />}
        {isStoveStep && (
          <StoveStepInputs index={index} isRequired={isStoveStep} />
        )}
      </Flex>

      <Form.Item
        name={[index, "description" satisfies keyof RecipeStepDTO]}
        rules={[{ required: true, message: "Input a description" }]}
        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
      >
        <Input.TextArea size={"large"} placeholder={"desciption"} />
      </Form.Item>
    </Flex>
  );
};
