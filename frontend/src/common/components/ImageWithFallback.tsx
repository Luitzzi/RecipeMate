import { Flex } from "antd";
import { ImageIcon } from "lucide-react";
import { useState } from "react";

import { tokens } from "#/theme/tokens.ts";

type Props = {
  src?: string;
  alt: string;
};

/**
 * Fills its parent with the image (cover). Shows a placeholder icon
 * if no src is given or the image fails to load.
 */
export const ImageWithFallback = ({ src, alt }: Props) => {
  const [failedSrc, setFailedSrc] = useState<string>();

  if (src && src !== failedSrc) {
    return (
      <img
        src={src}
        alt={alt}
        onError={() => setFailedSrc(src)}
        style={{
          width: "100%",
          height: "100%",
          objectFit: "cover",
          display: "block",
        }}
      />
    );
  }

  return (
    <Flex
      align="center"
      justify="center"
      style={{
        width: "100%",
        height: "100%",
        color: tokens.color.border.default,
      }}
    >
      <ImageIcon size={tokens.iconSize.large} />
    </Flex>
  );
};
