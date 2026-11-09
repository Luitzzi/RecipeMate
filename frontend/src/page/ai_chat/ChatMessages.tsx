import { Bubble } from "@ant-design/x";
import type { RoleType } from "@ant-design/x/es/bubble/interface";
import { CircleAlert } from "lucide-react";
import { useEffect, useRef, useState } from "react";

import { TypedMarkdown } from "#/common/components/TypedMarkdown.tsx";
import { tokens } from "#/theme/tokens.ts";
import type { ChatMessage } from "##/src/page/ai_chat/hooks/useChatSession.ts";

type Props = {
  messages: ChatMessage[];
  loading?: boolean;
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
    extra: (
      <CircleAlert size={tokens.iconSize.medium} color={tokens.color.error} />
    ),
  },
} satisfies RoleType;

const LOADING_ITEM = {
  content: "",
  role: "ai",
  key: "loading",
  loading: true,
};

export const ChatMessages = ({ messages, loading = false }: Props) => {
  // Count of messages restored from local storage.
  const [restoredCount] = useState(messages.length);

  const items = messages.map(({ role, content }, index) => ({
    key: String(index),
    role,
    content,
    extraInfo: { animate: index >= restoredCount }, // Do not animate messages restored from local storage.
  }));

  // Scroll to the bottom when the last message is from the user.
  const scrollRef = useRef<HTMLDivElement>(null);
  const last = messages.at(-1);

  useEffect(() => {
    if (last?.role === "user")
      scrollRef.current?.scrollTo({ top: 0, behavior: "smooth" });
  }, [last]);

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
