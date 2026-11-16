import { useApi } from "#/api/apiContext.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import type {
  ChatRequestUiDTO,
  CurrentViewUiDTO,
} from "#/generated/interface-api.ts";

import { useChatSession } from "./useChatSession";

export const useChatMessaging = (currentView: CurrentViewUiDTO | undefined) => {
  const {
    chatId,
    setChatId,
    messages,
    addMessage,
    setLastMessageError,
    resetChat,
  } = useChatSession();

  const api = useApi();
  const notify = useNotification();

  const onMessageError = (error: Error) => {
    notify.error({
      title: "Mate could not answer.",
      description: error.message,
    });
    setLastMessageError();
  };

  const startChat = async () => {
    const id = await api.chat.createChat();
    setChatId(id);
    return id;
  };

  const [requestAnswer, , loading] = useMutation(async (prompt: string) => {
    const id = chatId ?? (await startChat());

    const request = {
      message: prompt,
      currentView: currentView!,
    } satisfies ChatRequestUiDTO;
    return api.chat.sendMessage(id, request);
  });

  const sendMessage = async (prompt: string) => {
    addMessage("user", prompt);

    const response = await requestAnswer(prompt).catch(onMessageError);
    if (response) addMessage("ai", response);
  };

  return { messages, sendMessage, loading, resetChat };
};
