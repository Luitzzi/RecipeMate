import { FloatButton, Grid, Popover } from "antd";
// import { useAiCompanion } from "#/context/aiCompanionContext.tsx";
import { tokens } from "#/theme/tokens.ts";
import { useState } from "react";
import mate_head from "#/assets/mate/mate_head.png";

export const AiChat = () => {
  // TODO JPI: Messages in useState und sessionStorage speichern

  const [open, setOpen] = useState(false);
  const screens = Grid.useBreakpoint();
  const gutter = screens.md
    ? tokens.layout.gutter.desktop
    : tokens.layout.gutter.mobile;

  return (
    <>
      <Popover
        open={open}
        // TODO JPI: placement on different resolutions?
        placement={screens.xl ? "rightBottom" : "topRight"}
        trigger="click"
        title="Mate"
        styles={{
          root: { width: `min(480px, calc(100vw - ${2 * gutter}px))` },
          content: { maxHeight: "calc(100vh - 120px)", overflowY: "auto" },
        }}
        content={"MY CONTENT"}
      >
        <FloatButton
          type="primary"
          shape="square"
          icon={
            <img
              src={mate_head}
              alt="Mate"
              style={{
                width: "100%",
                padding: 4,
                boxSizing: "border-box",
                display: "block",
              }}
            />
          }
          onClick={() => setOpen(!open)}
          style={{
            insetInlineEnd: gutter,
            insetBlockEnd: gutter,
            width: 52,
            height: "auto",
            aspectRatio: 1,
            minHeight: 0,
          }}
        />
      </Popover>
    </>
  );
};
