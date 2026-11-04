import { FloatButton, Grid, Popover } from "antd";
import type { TextAreaRef } from "antd/es/input/TextArea";
import { useRef, useState } from "react";

import { useApi } from "#/api/apiContext.ts";
import mate_head from "#/assets/mate/mate_head.png";
import { ChatInput } from "#/common/components/input/ChatInput.tsx";
import { useAiChatView } from "#/common/handles/aiChatHandle.ts";
import { useChatHistory } from "#/common/hooks/useChatHistory.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import { useSessionStorage } from "#/common/hooks/useSessionStorage.ts";
import type { ChatRequestUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";

import { ChatHistory } from "./ChatHistory";

// TODO JPI: obige leiste: evtl blauer akzent, close button, new chat
// TODO JPI: mobile view: mate button ausblenden
// TODO JPI (Folder Page): call to action in der Mitte

const FLOAT_BUTTON_SIZE = 44;
const POPOVER_ARROW_GAP = 12;

export const AiChat = () => {
  // --- Popover State ---
  const [open, setOpen] = useState(false);
  const inputRef = useRef<TextAreaRef>(null);

  // --- Chat State ---
  const currentView = useAiChatView();
  const [chatId, setChatId] = useSessionStorage<string | undefined>(
    "ai-chat-id",
    undefined,
  );
  const { messages, addMessage, setLastMessageError } = useChatHistory();

  // --- API etc ---
  const api = useApi();
  const notify = useNotification();

  // TODO JPI: where to put
  const historyScrollRef = useRef<HTMLDivElement>(null);

  // --- Messaging logic ---
  const onMessageError = (error: Error) => {
    notify.error({
      title: "Mate could not answer.",
      description: error.message,
    });
    setLastMessageError();
  };

  const [requestAnswer, , loading] = useMutation(
    async (prompt: string) => {
      let id = chatId;
      if (!id) {
        id = await api.chat.createChat();
        setChatId(id);
      }

      const request = {
        message: prompt,
        currentView: currentView!,
      } satisfies ChatRequestUiDTO;

      return api.chat.sendMessage(id, request);
    },
    { onError: onMessageError },
  );

  const sendMessage = async (prompt: string) => {
    addMessage("user", prompt);
    historyScrollRef.current?.scrollTo({ top: 0, behavior: "smooth" });

    const response = await requestAnswer(prompt);
    if (response) addMessage("ai", response.message);
  };

  // --- Layout ---
  const screens = Grid.useBreakpoint();
  const gutter = screens.md
    ? tokens.layout.gutter.desktop
    : tokens.layout.gutter.mobile;

  if (!currentView) return null;
  return (
    <>
      <Popover
        open={open}
        afterOpenChange={(isOpen) => {
          if (isOpen) inputRef.current?.focus({ cursor: "end" });
        }}
        placement={screens.xl ? "rightBottom" : "topRight"}
        trigger="click"
        title="Mate"
        styles={{
          root: { width: `min(480px, calc(100vw - ${2 * gutter}px))` },
          container: {
            display: "flex",
            flexDirection: "column",
            maxHeight: `calc(100dvh - ${FLOAT_BUTTON_SIZE + POPOVER_ARROW_GAP + 2 * gutter}px)`,
            boxShadow: tokens.shadow.popup,
          },
          content: {
            display: "flex",
            flexDirection: "column",
            gap: tokens.gap.small,
            minHeight: 0,
          },
        }}
        content={
          <>
            <ChatHistory
              messages={messages}
              loading={loading}
              scrollRef={historyScrollRef}
            />

            <ChatInput
              sendMessage={sendMessage}
              placeholder="Send a message to Mate."
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
    </>
  );
};
