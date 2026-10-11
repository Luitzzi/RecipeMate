import type { ThemeConfig } from "antd";

/**
 * HolidayMate design tokens. Components read values from `tokens` only —
 * never from antd's `theme.useToken()`.
 * Principles and usage rules are documented in docs/design-tokens.md;
 * the values are taken from
 * docs/antd-docs/ant-d-design-system.md.
 */

/** Spacing scale on the 4 px grid, shared by margins, paddings and gaps. */
const spacing = {
    small: 8,
    medium: 16,
    large: 24,
};

export const tokens = {
    margin: spacing,
    padding: spacing,
    gap: spacing,

    /** Page frame spacing — the only spacing values outside the scale. */
    layout: {
        gutter: { mobile: spacing.medium, desktop: 32 },
        frameMargin: 32,
        maxWidth: 1200,
    },

    color: {
        primary: "#1677FF",
        success: "#52C41A",
        warning: "#FAAD14",
        error: "#FF4D4F",
        info: "#1677FF",
        text: {
            primary: "rgba(0, 0, 0, 0.88)",
            secondary: "rgba(0, 0, 0, 0.65)",
            tertiary: "rgba(0, 0, 0, 0.45)",
            disabled: "rgba(0, 0, 0, 0.25)",
        },
        background: {
            layout: "#F5F5F5",
            container: "#FFFFFF",
            subtle: "#FAFAFA",
        },
        border: {
            default: "#D9D9D9",
            subtle: "#F0F0F0",
        },
        /** Neutral fill for inactive indicators (e.g. an empty badge). */
        fill: "#F0F0F0",
    },

    fontFamily:
        "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif",
    fontSize: {
        small: 12,
        medium: 14,
        large: 16,
    },
    fontWeight: {
        regular: 400,
        strong: 600,
    },

    radius: {
        small: 4,
        medium: 6,
        large: 8,
    },

    /** Heights of antd controls for size="small" | "middle" | "large". */
    controlHeight: {
        small: 24,
        medium: 32,
        large: 40,
    },

    borderWidth: 1,

    shadow: {
        raised:
            "0 1px 2px 0 rgba(0, 0, 0, 0.05), 0 1px 6px -1px rgba(0, 0, 0, 0.03), 0 2px 4px 0 rgba(0, 0, 0, 0.03)",
        popup:
            "0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 9px 28px 8px rgba(0, 0, 0, 0.05)",
    },
};

/**
 * Feeds the seeds antd derives its component styles from, so antd
 * components match `tokens`. Ant Design's defaultAlgorithm derives every
 * other antd token (hover/active states, shadows, …) from these seeds.
 */
export const themeConfig: ThemeConfig = {
    token: {
        colorPrimary: tokens.color.primary,
        colorSuccess: tokens.color.success,
        colorWarning: tokens.color.warning,
        colorError: tokens.color.error,
        colorInfo: tokens.color.info,
        borderRadius: tokens.radius.medium,
        controlHeight: tokens.controlHeight.medium,
        fontFamily: tokens.fontFamily,
        fontSize: tokens.fontSize.medium,
    },
};
