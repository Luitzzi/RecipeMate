import type { StoveStepDTO } from "#/generated/interface-api.ts";
import { Form, Select } from "antd";
import { tokens } from "#/theme/tokens.ts";
import { HEAT_LEVEL_OPTIONS } from "#/common/enumOptions.ts";

export type StoveStepInputsProps = {
  index: number;
  isRequired: boolean;
};

export const StoveStepInputs = ({
  index,
  isRequired,
}: StoveStepInputsProps) => {
  return (
    <>
      <Form.Item
        name={[index, "heatLevel" satisfies keyof StoveStepDTO]}
        rules={[
          {
            required: isRequired,
            message: "Select the heat level",
          },
        ]}
        style={{ marginBottom: 0 }}
      >
        <Select
          size={"large"}
          placeholder={"Heat level"}
          options={HEAT_LEVEL_OPTIONS}
          style={{ width: tokens.width.selectField }}
        />
      </Form.Item>
    </>
  );
};
