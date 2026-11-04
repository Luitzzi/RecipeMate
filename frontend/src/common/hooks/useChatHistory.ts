import { useSessionStorage } from "#/common/hooks/useSessionStorage.ts";

export type ChatMessage = {
  role: "user" | "ai" | "user_error";
  content: string;
};

const INITIAL_MESSAGES = [
  {
    content: "Hi, I'm mate. How can I help you today?",
    role: "ai",
  },
] satisfies ChatMessage[];

export const useChatHistory = () => {
  const [messages, setMessages] = useSessionStorage<ChatMessage[]>(
    "ai-chat-history",
    INITIAL_MESSAGES,
  );

  const addMessage = (role: ChatMessage["role"], content: string) => {
    setMessages((prev) => [...prev, { role, content }]);
  };

  const setLastMessageError = () => {
    setMessages((prev) =>
      prev.length === 0
        ? prev
        : [...prev.slice(0, -1), { ...prev.at(-1)!, role: "user_error" }],
    );
  };

  return { messages, addMessage, setLastMessageError };
};
