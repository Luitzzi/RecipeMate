import { PictureOutlined } from "@ant-design/icons";
import { Carousel, Flex, Skeleton } from "antd";
import type { CSSProperties } from "react";
import { useFetch } from "#/common/hooks/useFetch.ts";
import { tokens } from "#/theme/tokens.ts";
import {useApi} from "#/api/apiContext.ts";

const imageFrameStyle: CSSProperties = {
  width: "100%",
  aspectRatio: "4 / 3",
  borderRadius: tokens.radius.large,
  background: tokens.color.background.subtle,
};

type RecipeImageCarouselProps = {
  imageKeys: string[];
  title: string;
};

/** Cycles through the recipe images, which are loaded via presigned download urls. */
export const RecipeImageCarousel = ({
  imageKeys,
  title,
}: RecipeImageCarouselProps) => {
  const api = useApi();
  const [imageUrls, loading] = useFetch(
    () =>
      Promise.all(
        imageKeys.map((key) =>
          api.image.getDownloadUrl({ key }).then((image) => image.url),
        ),
      ),
    [imageKeys],
  );

  if (imageKeys.length === 0) {
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

  if (loading || !imageUrls) {
    return (
      <Skeleton.Node active style={{ ...imageFrameStyle, height: "auto" }} />
    );
  }

  return (
    <Carousel arrows={imageUrls.length > 1} dots={imageUrls.length > 1}>
      {imageUrls.map((url, index) => (
        <div key={url}>
          <img
            src={url}
            alt={`${title} – Bild ${index + 1}`}
            style={{ ...imageFrameStyle, display: "block", objectFit: "cover" }}
          />
        </div>
      ))}
    </Carousel>
  );
};
