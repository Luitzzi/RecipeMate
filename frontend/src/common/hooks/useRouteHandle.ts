import { useMatches, type UIMatch } from "react-router";

export type RouteHandle = {
  aiChat?: boolean;
};

export default function useRouteHandle<K extends keyof RouteHandle>(key: K) {
  const matches = useMatches() as UIMatch<unknown, RouteHandle | undefined>[];
  return matches.some((m) => m.handle?.[key]);
}
