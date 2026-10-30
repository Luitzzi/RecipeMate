import { Form, InputNumber, Select, Switch } from "antd";
import type { OvenStepDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import { OVEN_MODE_OPTIONS } from "#/common/enumOptions.ts";

export type OvenStepInputsProps = {
  index: number;
  isRequired: boolean;
};

export const OvenStepInputs = ({ index, isRequired }: OvenStepInputsProps) => {
  return (
    <>
      <Form.Item
        name={[index, "ovenMode" satisfies keyof OvenStepDTO]}
        rules={[
          {
            required: isRequired,
            message: "Select the oven mode",
          },
        ]}
        style={{ marginBottom: 0 }}
      >
        <Select
          size={"large"}
          placeholder={"Oven mode"}
          options={OVEN_MODE_OPTIONS}
          style={{ width: tokens.width.selectField }}
        />
      </Form.Item>

      <Form.Item
        name={[index, "temperatureC" satisfies keyof OvenStepDTO]}
        rules={[
          {
            required: isRequired,
            message: "Input the oven temperature",
          },
        ]}
        style={{ marginBottom: 0 }}
      >
        <InputNumber
          size={"large"}
          placeholder={"Temperature"}
          min={0}
          max={300}
          style={{ width: tokens.width.numberField * 2 }}
        />
      </Form.Item>

      <Form.Item
        name={[index, "durationSeconds" satisfies keyof OvenStepDTO]}
        style={{ marginBottom: 0 }}
      >
        <InputNumber
          size={"large"}
          placeholder={"Duration"}
          min={0}
          style={{ width: tokens.width.numberField * 2 }}
        />
      </Form.Item>

      <Form.Item
        name={[index, "preheat" satisfies keyof OvenStepDTO]}
        style={{ marginBottom: 0 }}
        label={"Preheat"}
      >
        <Switch style={{ width: tokens.width.numberField / 2 }} />
      </Form.Item>
    </>
  );
};
