import { Button, Card, Dropdown, type MenuProps, Space } from "antd";
import { type ReactNode, useState } from "react";

import { ImageWithFallback } from "#/common/components/ImageWithFallback.tsx";
import { tokens } from "#/theme/tokens.ts";

export type MediaCardAction = {
  key?: string;
  icon: ReactNode;
  onClick?: () => void;
  // If set, the action opens a dropdown menu with these items
  menu?: MenuProps["items"];
};

export type MediaCardMeta = {
  key?: string;
  text: string;
  icon?: ReactNode;
};

type Props = {
  title: string;
  imageUrl?: string;
  actions?: MediaCardAction[];
  meta?: MediaCardMeta[];
  borderRadius?: number;
  dimensions?: {
    width?: number | string;
    height?: number | string;
  };
  onClick?: () => void;
};

export const MediaCard = ({
  title,
  imageUrl,
  actions = [],
  meta: descriptions = [],
  borderRadius = tokens.radius.medium,
  dimensions = {
    width: "200px",
    height: "200px",
  },
  onClick,
}: Props) => {
  const [hovered, setHovered] = useState(false);
  const highlighted = !!onClick && hovered;

  return (
    <Card
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        width: dimensions.width,
        height: dimensions.height,
        borderRadius: borderRadius,
        overflow: "hidden",
        boxShadow: highlighted ? tokens.shadow.popup : tokens.shadow.raised,
        borderColor: highlighted
          ? tokens.color.primary
          : tokens.color.border.subtle,
        cursor: onClick ? "pointer" : undefined,
        transition: "border-color 0.2s, box-shadow 0.2s",
      }}
      styles={{
        body: {
          padding: 0,
          height: "100%",
          display: "flex",
          flexDirection: "column",
        },
      }}
    >
      <div
        style={{
          position: "relative",
          flex: 1,
          minHeight: 0,
          background: tokens.color.fill,
        }}
      >
        <ImageWithFallback src={imageUrl} alt={title} />

        <Space
          size={tokens.gap.xsmall}
          style={{
            position: "absolute",
            top: tokens.margin.small,
            right: tokens.margin.small,
          }}
          orientation="vertical"
        >
          {actions.map((action, index) => {
            const button = (
              <Button
                key={action.key ?? index}
                icon={action.icon}
                onClick={(e) => {
                  e.stopPropagation();
                  action.onClick?.();
                }}
                shape="circle"
                onMouseEnter={() => setHovered(false)}
                onMouseLeave={() => setHovered(true)}
              />
            );

            return action.menu ? (
              <Dropdown
                key={action.key ?? index}
                trigger={["click"]}
                placement={"bottomRight"}
                menu={{
                  items: action.menu,
                  // Prevent menu clicks from bubbling to the card through the portal
                  onClick: ({ domEvent }) => domEvent.stopPropagation(),
                }}
              >
                {button}
              </Dropdown>
            ) : (
              button
            );
          })}
        </Space>
      </div>

      <div
        style={{
          flex: "none",
          background: tokens.color.background.container,
          paddingBlock: tokens.padding.small,
          paddingInline: tokens.padding.small,
          display: "flex",
          flexDirection: "column",
          gap: tokens.gap.xsmall,
          lineHeight: 1,
        }}
      >
        <h3 style={{ margin: 0, lineHeight: 1 }}>{title}</h3>
        <Space size={tokens.gap.small} wrap>
          {descriptions.map((desc, index) => (
            <Space
              key={desc.key ?? index}
              size={tokens.gap.xsmall}
              align="center"
            >
              {desc.icon && (
                <span style={{ display: "flex" }}>{desc.icon}</span>
              )}
              <span>{desc.text}</span>
            </Space>
          ))}
        </Space>
      </div>
    </Card>
  );
};
