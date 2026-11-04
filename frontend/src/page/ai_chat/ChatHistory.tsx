import { ExclamationCircleOutlined } from "@ant-design/icons";
import { Bubble } from "@ant-design/x";
import type { RoleType } from "@ant-design/x/es/bubble/interface";
import { type Ref, useState } from "react";

import { TypedMarkdown } from "#/common/components/TypedMarkdown.tsx";
import type { ChatMessage } from "#/common/hooks/useChatHistory.ts";
import { tokens } from "#/theme/tokens.ts";

type Props = {
  messages: ChatMessage[];
  loading?: boolean;
  scrollRef?: Ref<HTMLDivElement>;
};

const ROLES = {
  ai: {
    placement: "start",
    variant: "outlined",
    styles: {
      content: {
        border: `${tokens.borderWidth}px solid ${tokens.color.border.default}`,
      },
    },
    contentRender: (content, { extraInfo }) => (
      <TypedMarkdown content={content as string} animate={extraInfo?.animate} />
    ),
  },
  user: {
    placement: "end",
  },
  user_error: {
    placement: "end",
    variant: "outlined",
    styles: {
      content: {
        border: `${tokens.borderWidth}px solid ${tokens.color.error}`,
        color: tokens.color.text.secondary,
      },
      extra: { alignSelf: "center", display: "flex" },
    },
    extra: <ExclamationCircleOutlined style={{ color: tokens.color.error }} />,
  },
} satisfies RoleType;

const LOADING_ITEM = {
  content: "",
  role: "ai",
  key: "loading",
  loading: true,
};

export const ChatHistory = ({
  messages,
  loading = false,
  scrollRef,
}: Props) => {
  // Messages present on mount are shown at once, only new ones are typed.
  const [restoredCount] = useState(messages.length);
  const items = messages.map(({ role, content }, index) => ({
    key: String(index),
    role,
    content,
    extraInfo: { animate: index >= restoredCount },
  }));

  return (
    <div
      style={{
        minHeight: 0,
        overflowY: "auto",
        display: "flex",
        flexDirection: "column-reverse",
      }}
      ref={scrollRef}
    >
      <Bubble.List
        items={loading ? [...items, LOADING_ITEM] : items}
        role={ROLES}
      />
    </div>
  );
};
