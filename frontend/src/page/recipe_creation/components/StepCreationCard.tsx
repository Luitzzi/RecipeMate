import { type RecipeStepDTO, StepMethod } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import {
  getEmptyStep,
  RECIPE_CREATION_FORM_FIELDS,
} from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import { Button, Flex, Form, Input, InputNumber, Select } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { useState } from "react";
import { OvenStepInputs } from "#/page/recipe_creation/components/step_components/OvenStepInputs.tsx";
import { StoveStepInputs } from "#/page/recipe_creation/components/step_components/StoveStepInputs.tsx";
import { STEP_OPTIONS } from "#/common/enumOptions.ts";

export const StepCreationCard = () => {
  const [stepMethod, setStepMethod] = useState<StepMethod>();
  const isOvenStep = stepMethod === StepMethod.OVEN;
  const isStoveStep = stepMethod === StepMethod.STOVE;

  return (
    <Form.List name={RECIPE_CREATION_FORM_FIELDS.steps}>
      {(fields, { add, remove }) => (
        <Flex vertical gap={tokens.gap.small}>
          {fields.map(({ key, name }) => (
            <Flex
              key={key}
              vertical
              gap={tokens.gap.small}
              style={{
                border: tokens.border,
                padding: tokens.padding.small,
              }}
            >
              <Flex gap={tokens.gap.small} align={"center"}>
                <Form.Item
                  name={[name, "stepMethod" satisfies keyof RecipeStepDTO]}
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
                {isOvenStep && (
                  <OvenStepInputs index={name} isRequired={isOvenStep} />
                )}
                {isStoveStep && (
                  <StoveStepInputs index={name} isRequired={isStoveStep} />
                )}
              </Flex>

              <Form.Item
                name={[name, "description" satisfies keyof RecipeStepDTO]}
                rules={[{ required: true, message: "Input a description" }]}
                style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
              >
                <Input.TextArea size={"large"} placeholder={"desciption"} />
              </Form.Item>
            </Flex>
          ))}
          <Button
            type="dashed"
            size="large"
            block
            icon={<PlusOutlined />}
            onClick={() => add(getEmptyStep())}
          >
            Add step
          </Button>
        </Flex>
      )}
    </Form.List>
  );
};
