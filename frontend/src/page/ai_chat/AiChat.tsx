import { FloatButton, Grid, Popover } from "antd";
import type { TextAreaRef } from "antd/es/input/TextArea";
import { useRef, useState } from "react";

import mate_head from "#/assets/mate/mate_head.webp";
import { tokens } from "#/theme/tokens.ts";
import { useAiChatCurrentView } from "##/src/common/hooks/useAiChatCurrentView.ts";

import { ChatHeader } from "./ChatHeader";
import { ChatInput } from "./ChatInput";
import { ChatMessages } from "./ChatMessages";
import { useChatMessaging } from "./hooks/useChatMessaging";

// TODO JPI: mobile view (in navigationsleiste unten?)

const FLOAT_BUTTON_SIZE = 44;
const POPOVER_ARROW_GAP = 12;

export const AiChat = () => {
  // --- Popover State ---
  const [open, setOpen] = useState(false);
  const inputRef = useRef<TextAreaRef>(null);

  // --- Chat ---
  const currentView = useAiChatCurrentView();
  const { messages, sendMessage, loading, resetChat } =
    useChatMessaging(currentView);

  // --- Layout ---
  const screens = Grid.useBreakpoint();
  const gutter = screens.md
    ? tokens.layout.gutter.desktop
    : tokens.layout.gutter.mobile;

  if (!currentView) return null;
  return (
    <Popover
      open={open}
      afterOpenChange={(isOpen) => {
        if (isOpen) inputRef.current?.focus({ cursor: "end" });
      }}
      placement={screens.xl ? "rightBottom" : "topRight"}
      trigger="click"
      title={
        <ChatHeader
          onNewChat={resetChat}
          onClose={() => setOpen(false)}
          newChatDisabled={loading}
        />
      }
      styles={{
        root: { width: `min(480px, calc(100vw - ${2 * gutter}px))` },
        container: {
          display: "flex",
          flexDirection: "column",
          maxHeight: `calc(100dvh - ${FLOAT_BUTTON_SIZE + POPOVER_ARROW_GAP + 2 * gutter}px)`,
          boxShadow: tokens.shadow.popup,
          padding: 0,
          overflow: "hidden",
        },
        title: {
          margin: 0,
          padding: `${tokens.padding.small}px ${tokens.padding.medium}px`,
          background: tokens.color.primary,
          color: tokens.color.text.onPrimary,
        },
        content: {
          display: "flex",
          flexDirection: "column",
          gap: tokens.gap.small,
          minHeight: 0,
          padding: tokens.padding.medium,
        },
      }}
      content={
        <>
          <ChatMessages messages={messages} loading={loading} />

          <ChatInput
            sendMessage={sendMessage}
            placeholder="Send a message to Mate."
            disabled={loading}
            ref={inputRef}
          />
        </>
      }
    >
      <FloatButton
        type="primary"
        shape="square"
        icon={
          <img
            src={mate_head}
            alt="Mate"
            style={{
              width: "100%",
              padding: 4,
              boxSizing: "border-box",
              display: "block",
            }}
          />
        }
        onClick={() => setOpen(!open)}
        style={{
          insetInlineEnd: gutter,
          insetBlockEnd: gutter,
          width: FLOAT_BUTTON_SIZE,
          height: "auto",
          aspectRatio: 1,
          minHeight: 0,
        }}
      />
    </Popover>
  );
};
