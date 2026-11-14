import { type Params, type UIMatch, useMatches } from "react-router";

import { type CurrentViewUiDTO, ViewType } from "#/generated/interface-api.ts";

/**
 * Route `handle` that enables the AI chat on a route.
 * `aiChat` maps the route params to the view context sent to the AI.
 */
export type AiChatRouteHandle = {
  aiChat?: (params: Params) => CurrentViewUiDTO;
};

/** Predefined handles for routes, e.g. `<Route handle={AI_CHAT_HANDLE.recipe} />`. */
export const AI_CHAT_HANDLE = {
  generic: { aiChat: () => ({ type: ViewType.GENERIC }) },
  recipe: {
    aiChat: ({ id }) => ({ type: ViewType.RECIPE, id: Number(id) }),
  },
} satisfies Record<string, AiChatRouteHandle>;

/**
 * Resolves the AI chat view context of the current route.
 *
 * @returns The view context built by the deepest matched route's `aiChat` handle,
 *   or `undefined` if no matched route has one (the AI chat is disabled on this route).
 */
export const useAiChatCurrentView = () => {
  const matches = useMatches() as UIMatch<
    unknown,
    AiChatRouteHandle | undefined
  >[];
  // Last match = deepest route, so nested routes override their parents.
  const match = matches.findLast((m) => m.handle?.aiChat);
  return match?.handle?.aiChat?.(match.params);
};
