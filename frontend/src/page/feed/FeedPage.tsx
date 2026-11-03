import { tokens } from "#/theme/tokens.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { DocsNav } from "#/page/docs/DocsLayout.tsx";
import { Button, Card, Flex, Input } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { Meta } from "antd/es/list/Item";

export default function FeedPage() {
  return (
    <Flex vertical style={{ paddingBottom: tokens.padding.large }}>
      <Input.Search size="large" placeholder="Filled" variant="filled" />

      <SectionHeader
        title="Rezepte Feed"
        description="Living reference for docs/design-tokens.md. Every example reads its values from src/theme/tokens.ts."
      />

      {/* Infinity Scrolls */}

      <div>
        <SectionHeader
          title="Noodles"
          description="Living reference for docs/design-tokens.md. Every example reads its values from src/theme/tokens.ts."
        />

        <Flex style={{ overflowX: "scroll" }} gap={tokens.padding.large}>
          <FeedPageCard />
          <FeedPageCard />
          <FeedPageCard />
          <FeedPageCard />
          <FeedPageCard />
        </Flex>
      </div>

      <SectionHeader
        title="Meat"
        description="Living reference for docs/design-tokens.md. Every example reads its values from src/theme/tokens.ts."
      />
    </Flex>
  );
}

const FeedPageCard = () => (
  <Card
    hoverable
    variant="borderless"
    styles={{
      root: {
        boxShadow: "none",
        background: "#f2f2f2",
        minWidth: "14rem",
        overflow: "hidden",
      },
      cover: { position: "relative", backgroundSize: "cover", height: "100%" },
      body: {
        padding: "0 1rem",
        fontSize: tokens.fontSize.large,
        position: "absolute",
        bottom: "0",
        background: "linear-gradient(to top, black, transparent)",
        width: "100%",
        color: "white",
        margin: "0",
      },
    }}
    cover={
      <img
        style={{
          aspectRatio: "1/1",
          backgroundSize: "cover",
          objectFit: "cover",
        }}
        draggable={false}
        alt="example"
        src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fkochkarussell.com%2Fwp-content%2Fuploads%2F2023%2F10%2FBolognese-1.jpg&f=1&nofb=1&ipt=3768ff5a431ebe49a47343ab006b235b4cda5a1bdb03f0bd384e0c51ff71a694&ipo=images"
      />
    }
  >
    <Meta title="Bolognese" />
  </Card>
);
