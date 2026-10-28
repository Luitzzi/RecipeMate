import { Col, Flex, Row, Segmented, Typography } from "antd";
import type { ReactNode } from "react";
import { useLocation, useNavigate } from "react-router";
import { tokens } from "../../theme/tokens.ts";
import { panelStyle } from "./docsStyles.ts";
import { TitleDivider } from "../../common/components/SectionHeader.tsx";

/** Building blocks shared by the documentation pages. */

export const Caption = ({ children }: { children: ReactNode }) => (
  <Typography.Text type="secondary" style={{ fontSize: tokens.fontSize.small }}>
    {children}
  </Typography.Text>
);

export const TokenLabel = ({
  name,
  value,
}: {
  name: string;
  value: ReactNode;
}) => (
  <Flex vertical>
    <Typography.Text code style={{ fontSize: tokens.fontSize.small }}>
      {name}
    </Typography.Text>
    <Caption>{value}</Caption>
  </Flex>
);

type ExampleProps = {
  title: string;
  description: ReactNode;
  usage?: string;
  children: ReactNode;
};

/** One reference entry: live example, with the rule on the side. */
export const Example = ({
  title,
  description,
  usage,
  children,
}: ExampleProps) => (
  <Row gutter={[tokens.gap.large, tokens.gap.medium]}>
    <Col xs={24} md={14}>
      <div style={panelStyle}>{children}</div>
    </Col>
    <Col xs={24} md={10}>
      <Flex vertical gap={tokens.gap.small}>
        <Typography.Text strong>{title}</Typography.Text>
        <Typography.Text type="secondary">{description}</Typography.Text>
        {usage && (
          <Typography.Text code style={{ fontSize: tokens.fontSize.small }}>
            {usage}
          </Typography.Text>
        )}
      </Flex>
    </Col>
  </Row>
);

export const Section = ({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) => (
  <Flex
    vertical
    gap={tokens.gap.large}
    style={{ marginTop: tokens.margin.large }}
  >
    <TitleDivider title={title} level={5} />
    {children}
  </Flex>
);

const DOCS_PAGES = [
  { value: "/docs", label: "Tokens" },
  { value: "/docs/components", label: "Components" },
];

/** Switches between the documentation pages; used as SectionHeader extra. */
export const DocsNav = () => {
  const { pathname } = useLocation();
  const navigate = useNavigate();

  return (
    <Segmented<string>
      options={DOCS_PAGES}
      value={pathname}
      onChange={navigate}
    />
  );
};
