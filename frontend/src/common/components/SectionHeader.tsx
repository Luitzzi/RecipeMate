import { Divider, Flex, Typography } from "antd";
import type { ReactNode } from "react";
import { tokens } from "../../theme/tokens.ts";

type TitleDividerProps = {
    title: string;
    level?: 4 | 5;
    extra?: ReactNode;
};

/** Divider with a primary colored title, as used for page and section titles. */
export const TitleDivider = ({
                                 title,
                                 level = 4,
                                 extra,
                             }: TitleDividerProps) => {
    return (
        <Flex align="center" gap={tokens.gap.small}>
            <Divider
                styles={{
                    root: { flex: 1, width: "auto", minWidth: 0 },
                    rail: { borderTopColor: tokens.color.primary },
                    content: { color: tokens.color.primary },
                }}
            >
                <Typography.Title level={level} style={{ margin: 0, color: "inherit" }}>
                    {title}
                </Typography.Title>
            </Divider>
            {extra}
        </Flex>
    );
};

type SectionHeaderProps = {
    title: string;
    description?: ReactNode;
    /** Actions rendered next to the title, e.g. a Button or Segmented. */
    extra?: ReactNode;
};

/** Page header: primary colored title with optional actions and description. */
export const SectionHeader = ({
                                  title,
                                  description,
                                  extra,
                              }: SectionHeaderProps) => {
    return (
        <>
            <TitleDivider title={title} extra={extra} />
            {description && (
                <Typography.Paragraph
                    type="secondary"
                    style={{ fontSize: tokens.fontSize.large }}
                >
                    {description}
                </Typography.Paragraph>
            )}
        </>
    );
};
