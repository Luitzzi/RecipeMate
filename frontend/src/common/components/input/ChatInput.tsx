import { SendOutlined } from "@ant-design/icons";
import { Button, Flex, Form, Input } from "antd";
import type { TextAreaRef } from "antd/es/input/TextArea";
import {
  type KeyboardEvent,
  type Ref,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from "react";

import { tokens } from "#/theme/tokens.ts";

type Props = {
  sendMessage?: (message: string) => void;
  placeholder?: string;
  ref?: Ref<TextAreaRef>;
};

export const ChatInput = ({ sendMessage, placeholder, ref }: Props) => {
  const [sendEnabled, setSendEnabled] = useState(false);

  const [form] = Form.useForm();
  const values = Form.useWatch([], form);

  const inputRef = useRef<TextAreaRef>(null);
  useImperativeHandle(ref, () => inputRef.current!, []);

  useEffect(() => {
    form
      .validateFields({ validateOnly: true })
      .then(() => setSendEnabled(true))
      .catch(() => setSendEnabled(false));
  }, [form, values]);

  const onSubmitMessage = ({ message }: { message: string }) => {
    if (sendMessage) sendMessage(message.trim());
    form.setFieldValue("message", "");
    inputRef.current?.focus({ cursor: "end" });
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    // Send message on Enter (except on Shift+Enter)
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      form.submit();
    }
  };

  return (
    <Flex gap={tokens.gap.small}>
      <Form
        form={form}
        onFinish={onSubmitMessage}
        validateTrigger={[]}
        style={{ flex: 1 }}
      >
        <Form.Item
          name="message"
          style={{ marginBottom: 0 }}
          rules={[
            {
              required: true,
              whitespace: true,
            },
          ]}
        >
          <Input.TextArea
            autoSize={{ minRows: 1, maxRows: 6 }}
            placeholder={placeholder ?? "Send a message."}
            onKeyDown={handleKeyDown}
            ref={inputRef}
          />
        </Form.Item>
      </Form>
      <Button
        type="primary"
        shape="square"
        disabled={!sendEnabled}
        onClick={form.submit}
        icon={<SendOutlined />}
      />
    </Flex>
  );
};
