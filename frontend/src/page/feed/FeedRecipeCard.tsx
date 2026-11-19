import { PictureOutlined } from "@ant-design/icons";
import { Card, Flex } from "antd";
import type { CSSProperties } from "react";
import { useNavigate } from "react-router";

import type { RecipeSummaryUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";

// TODO: No design token exists for these values yet.
const CARD_WIDTH = "14rem";
const TITLE_OVERLAY = "linear-gradient(to top, black, transparent)";

const imageFrameStyle: CSSProperties = {
  width: "100%",
  aspectRatio: "1 / 1",
  background: tokens.color.background.subtle,
};

type FeedRecipeCardProps = {
  recipe: RecipeSummaryUiDTO;
};

export const FeedRecipeCard = ({ recipe }: FeedRecipeCardProps) => {
  const navigate = useNavigate();

  return (
    <Card
      hoverable
      variant="borderless"
      onClick={() => navigate(`/recipe/${recipe.id}`)}
      styles={{
        root: {
          boxShadow: "none",
          background: tokens.color.fill,
          minWidth: CARD_WIDTH,
          width: CARD_WIDTH,
          overflow: "hidden",
          position: "relative",
        },
        body: {
          padding: `${tokens.padding.medium}px ${tokens.padding.large}px`,
          position: "absolute",
          bottom: 0,
          width: "100%",
          background: TITLE_OVERLAY,
        },
      }}
      cover={<RecipeCoverImage url={recipe.coverImageUrl} />}
    >
      <Card.Meta
        title={recipe.title}
        styles={{
          title: {
            color: tokens.color.text.onPrimary,
            fontSize: tokens.fontSize.large,
          },
        }}
      />
    </Card>
  );
};

const RecipeCoverImage = ({ url }: { url?: string }) => {
  if (!url) {
    return (
      <Flex align="center" justify="center" style={imageFrameStyle}>
        <PictureOutlined
          style={{
            fontSize: tokens.controlHeight.large,
            color: tokens.color.text.disabled,
          }}
        />
      </Flex>
    );
  }

  return (
    <img
      src={url}
      alt=""
      loading="lazy"
      draggable={false}
      style={{ ...imageFrameStyle, display: "block", objectFit: "cover" }}
    />
  );
};
