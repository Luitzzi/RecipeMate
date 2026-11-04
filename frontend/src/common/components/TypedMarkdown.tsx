import XMarkdown from "@ant-design/x-markdown";
import { useEffect, useState } from "react";

const TYPING_STEP = 6;
const TYPING_INTERVAL_MS = 20;

type Props = {
  content: string;
  animate?: boolean;
};

export const TypedMarkdown = ({ content, animate = false }: Props) => {
  const [length, setLength] = useState(animate ? 0 : content.length);
  const typing = length < content.length;

  useEffect(() => {
    if (!typing) return;
    const timeout = setTimeout(
      () => setLength((prev) => prev + TYPING_STEP),
      TYPING_INTERVAL_MS,
    );
    return () => clearTimeout(timeout);
  }, [length, typing]);

  return (
    <XMarkdown
      content={content.slice(0, length)}
      streaming={{
        hasNextChunk: typing,
        incompleteMarkdown: "complete",
        tail: true,
      }}
    />
  );
};
