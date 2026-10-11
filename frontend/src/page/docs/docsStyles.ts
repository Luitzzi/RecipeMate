import type { CSSProperties } from "react";
import { tokens } from "../../theme/tokens.ts";

/** Container around a live example. */
export const panelStyle: CSSProperties = {
  height: "100%",
  padding: tokens.padding.large,
  border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
  borderRadius: tokens.radius.large,
  background: tokens.color.background.container,
};

/** Placeholder block used to visualise space and structure. */
export const blockStyle: CSSProperties = {
  padding: tokens.padding.small,
  border: `${tokens.borderWidth}px solid ${tokens.color.border.default}`,
  borderRadius: tokens.radius.small,
  background: tokens.color.background.subtle,
};
