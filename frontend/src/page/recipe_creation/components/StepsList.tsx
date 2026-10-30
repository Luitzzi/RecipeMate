import { RECIPE_CREATION_FORM_FIELDS } from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import { Button, Flex, Form } from "antd";
import { StepCard } from "#/page/recipe_creation/components/step_components/StepCard.tsx";
import { PlusOutlined } from "@ant-design/icons";
import { tokens } from "#/theme/tokens.ts";

export const StepsList = () => {
  return (
    <Form.List name={RECIPE_CREATION_FORM_FIELDS.steps}>
      {(fields, { add, remove }) => (
        <Flex vertical gap={tokens.gap.small}>
          {fields.map(({ key, name }) => (
            <StepCard key={key} index={name} />
          ))}
          <Button
            type="dashed"
            size="large"
            block
            icon={<PlusOutlined />}
            onClick={() => add({})}
          >
            Add step
          </Button>
        </Flex>
      )}
    </Form.List>
  );
};
