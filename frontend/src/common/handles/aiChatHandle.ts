import { useMatches, type Params, type UIMatch } from "react-router";
import { ViewType, type CurrentViewUiDTO } from "#/generated/interface-api.ts";

export type AiChatRouteHandle = {
  aiChat?: (params: Params) => CurrentViewUiDTO;
};

export const AI_CHAT_HANDLE = {
  generic: { aiChat: () => ({ type: ViewType.GENERIC }) },
  recipe: {
    aiChat: ({ id }) => ({ type: ViewType.RECIPE, id: Number(id) }),
  },
} satisfies Record<string, AiChatRouteHandle>;

export const useAiChatView = () => {
  const matches = useMatches() as UIMatch<
    unknown,
    AiChatRouteHandle | undefined
  >[];
  const match = matches.findLast((m) => m.handle?.aiChat);
  return match?.handle?.aiChat?.(match.params);
};
