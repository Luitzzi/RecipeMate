import { Badge, Button, Col, Flex, Grid, Row, Tag, Typography } from "antd";
import { Fragment } from "react";
import { tokens } from "../../theme/tokens.ts";
import {
  Caption,
  DocsNav,
  Example,
  Section,
  TokenLabel,
} from "./DocsLayout.tsx";
import { blockStyle, panelStyle } from "./docsStyles.ts";
import {SectionHeader} from "../../common/components/SectionHeader.tsx";

/**
 * Living reference for docs/design-tokens.md: every example is rendered
 * from `tokens`, so the page always shows the current values.
 */

const SCALE = ["small", "medium", "large"] as const;

const ANTD_SIZE = {
  small: "small",
  medium: "middle",
  large: "large",
} as const;

/** Vertical measure whose height is the visualized space. */
const Ruler = ({ height, label }: { height: number; label: string }) => (
  <Flex align="center" gap={tokens.gap.small} style={{ height }}>
    <div
      style={{
        alignSelf: "stretch",
        width: tokens.borderWidth * 2,
        background: tokens.color.primary,
      }}
    />
    <Caption>{label}</Caption>
  </Flex>
);

const Swatch = ({ name, value }: { name: string; value: string }) => (
  <Flex vertical gap={tokens.gap.small}>
    <div
      style={{
        height: tokens.controlHeight.large,
        background: value,
        border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
        borderRadius: tokens.radius.medium,
      }}
    />
    <TokenLabel name={name} value={value} />
  </Flex>
);

const SwatchGrid = ({
  group,
  colors,
}: {
  group: string;
  colors: Record<string, string>;
}) => (
  <Row gutter={[tokens.gap.medium, tokens.gap.medium]}>
    {Object.entries(colors).map(([key, value]) => (
      <Col key={key} xs={12} sm={8}>
        <Swatch name={`${group}${key}`} value={value} />
      </Col>
    ))}
  </Row>
);

/** Purpose of every color token, keyed by its path in `tokens`. */
const COLOR_PURPOSE: Record<string, string> = {
  "color.primary":
    "Main actions, links, focus rings, selected navigation and section titles. Use for the single most important action per screen.",
  "color.success": "Positive feedback: saved, completed, valid.",
  "color.warning": "Caution that needs attention but does not block the user.",
  "color.error": "Errors, failed validation and destructive actions.",
  "color.info":
    "Neutral informational feedback. Same hue as primary, but never for actions.",
  "color.text.primary": "Body text, headings and input values.",
  "color.text.secondary": "Descriptions, supporting text and page intros.",
  "color.text.tertiary": "Hints, captions and meta information.",
  "color.text.disabled":
    "Placeholders, disabled controls and inactive content.",
  "color.background.layout":
    "Page background behind the frame — the bottom surface layer.",
  "color.background.container":
    "Surfaces holding content: page frame, panels, forms, sticky bars.",
  "color.background.subtle": "Table headers, hover states and muted blocks.",
  "color.border.default": "Outlines of controls and interactive boxes.",
  "color.border.subtle":
    "Dividers, the header separator, page frame and panel borders.",
  "color.fill": "Inactive indicators, e.g. an empty count badge.",
};

type ColorTree = { [key: string]: string | ColorTree };

/** Every color token as [path, value], so new tokens appear automatically. */
const flattenColors = (colors: ColorTree, prefix: string): [string, string][] =>
  Object.entries(colors).flatMap(([key, value]) =>
    typeof value === "string"
      ? [[`${prefix}.${key}`, value] as [string, string]]
      : flattenColors(value, `${prefix}.${key}`),
  );

const ColorOverview = () => (
  <Flex vertical gap={tokens.gap.medium}>
    <Flex vertical gap={tokens.gap.small}>
      <Typography.Text strong>All colors</Typography.Text>
      <Typography.Text type="secondary">
        Every color token with its purpose. Pick a color by the purpose that
        fits, not by its look. A token without a documented purpose is marked —
        add it to COLOR_PURPOSE and docs/design-tokens.md.
      </Typography.Text>
    </Flex>
    <div style={panelStyle}>
      {flattenColors(tokens.color, "color").map(([name, value], index) => (
        <Row
          key={name}
          gutter={[tokens.gap.medium, tokens.gap.small]}
          align="middle"
          style={{
            paddingBlock: tokens.padding.small,
            borderTop:
              index > 0
                ? `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`
                : undefined,
          }}
        >
          <Col xs={24} sm={10}>
            <Flex align="center" gap={tokens.gap.medium}>
              <div
                style={{
                  flexShrink: 0,
                  width: tokens.controlHeight.large,
                  height: tokens.controlHeight.large,
                  background: value,
                  border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
                  borderRadius: tokens.radius.medium,
                }}
              />
              <TokenLabel name={name} value={value} />
            </Flex>
          </Col>
          <Col xs={24} sm={14}>
            {COLOR_PURPOSE[name] ? (
              <Typography.Text>{COLOR_PURPOSE[name]}</Typography.Text>
            ) : (
              <Typography.Text type="warning">
                No purpose documented.
              </Typography.Text>
            )}
          </Col>
        </Row>
      ))}
    </div>
  </Flex>
);

const SpacingSection = () => (
  <Section title="Spacing">
    <Example
      title="One scale, three steps"
      description="margin, padding and gap share the same scale on the 4 px grid. Changing a step in tokens.ts changes it everywhere. There is no fourth step — if a design needs one, revisit the design."
    >
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "max-content 1fr",
          alignItems: "center",
          gap: tokens.gap.medium,
        }}
      >
        {SCALE.map((size) => (
          <Fragment key={size}>
            <TokenLabel name={size} value={`${tokens.gap[size]} px`} />
            <div
              style={{
                width: tokens.gap[size],
                height: tokens.controlHeight.small,
                background: tokens.color.primary,
                borderRadius: tokens.radius.small,
              }}
            />
          </Fragment>
        ))}
      </div>
    </Example>

    <Example
      title="Margin — space between blocks"
      description="Separates independent blocks. Use margin.large between page sections, margin.medium between form fields."
      usage="style={{ marginTop: tokens.margin.large }}"
    >
      <div style={blockStyle}>Section A</div>
      <Ruler height={tokens.margin.large} label="margin.large · 24 px" />
      <div style={blockStyle}>Section B</div>
    </Example>

    <Example
      title="Padding — space inside a container"
      description="The shaded area is the padding. Use padding.medium for regular containers, padding.large for panels and cards, padding.small for dense elements."
      usage="style={{ padding: tokens.padding.medium }}"
    >
      <Flex wrap gap={tokens.gap.medium}>
        {SCALE.map((size) => (
          <div
            key={size}
            style={{
              padding: tokens.padding[size],
              background: tokens.color.fill,
              borderRadius: tokens.radius.medium,
            }}
          >
            <div
              style={{
                ...blockStyle,
                background: tokens.color.background.container,
              }}
            >
              padding.{size}
            </div>
          </div>
        ))}
      </Flex>
    </Example>

    <Example
      title="Gap — space between siblings"
      description="Preferred over margins inside Flex and Row. gap.small for related items (tags, input rows), gap.medium for fields, gap.large for sections."
      usage="<Flex gap={tokens.gap.small}>"
    >
      <Flex vertical gap={tokens.gap.medium}>
        {SCALE.map((size) => (
          <Flex key={size} align="center" gap={tokens.gap.medium} wrap>
            <Flex gap={tokens.gap[size]}>
              {["Tent", "Map", "Torch"].map((item) => (
                <Tag key={item} style={{ marginInlineEnd: 0 }}>
                  {item}
                </Tag>
              ))}
            </Flex>
            <Caption>
              gap.{size} · {tokens.gap[size]} px
            </Caption>
          </Flex>
        ))}
      </Flex>
    </Example>
  </Section>
);

const LayoutSection = () => {
  const screens = Grid.useBreakpoint();
  const gutter = screens.md
    ? tokens.layout.gutter.desktop
    : tokens.layout.gutter.mobile;

  return (
    <Section title="Layout">
      <Example
        title="Page frame"
        description={
          <>
            The only spacing outside the three-step scale. From the md
            breakpoint the page is a framed box: frameMargin around it,
            gutter.desktop inside, at most maxWidth ({tokens.layout.maxWidth}{" "}
            px) wide. On phones the frame is removed and gutter.mobile is used.
            Only RootLayout uses these tokens.
          </>
        }
        usage="tokens.layout.gutter.desktop"
      >
        <div
          style={{
            padding: tokens.layout.frameMargin,
            background: tokens.color.background.layout,
            borderRadius: tokens.radius.medium,
          }}
        >
          <div
            style={{
              padding: gutter,
              background: tokens.color.background.container,
              border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
              borderRadius: tokens.radius.large,
              boxShadow: tokens.shadow.raised,
            }}
          >
            <Flex vertical gap={tokens.gap.small} style={blockStyle}>
              <Typography.Text strong>Page content</Typography.Text>
              <Caption>
                Outer: frameMargin · {tokens.layout.frameMargin} px
              </Caption>
              <Caption>
                Inner: {screens.md ? "gutter.desktop" : "gutter.mobile"} ·{" "}
                {gutter} px (active on this screen)
              </Caption>
            </Flex>
          </div>
        </div>
      </Example>
    </Section>
  );
};

const ColorSection = () => {
  const { primary, success, warning, error, info } = tokens.color;

  return (
    <Section title="Colors">
      <ColorOverview />

      <Example
        title="Brand and status"
        description="primary marks actions, links and selection — use it for the single most important action per screen. success / warning / error / info are for feedback only."
        usage="tokens.color.primary"
      >
        <Flex vertical gap={tokens.gap.medium}>
          <SwatchGrid
            group="color."
            colors={{ primary, success, warning, error, info }}
          />
          <Flex gap={tokens.gap.small}>
            <Button type="primary">Save activity</Button>
            <Button>Cancel</Button>
          </Flex>
        </Flex>
      </Example>

      <Example
        title="Text"
        description="Transparent black, so text blends on tinted surfaces. primary for body, secondary for descriptions, tertiary for hints, disabled for placeholders and inactive content."
        usage="tokens.color.text.secondary"
      >
        <Flex vertical gap={tokens.gap.small}>
          {Object.entries(tokens.color.text).map(([key, value]) => (
            <Flex key={key} justify="space-between" gap={tokens.gap.medium}>
              <Typography.Text style={{ color: value }}>
                Pack what you actually need
              </Typography.Text>
              <Typography.Text code style={{ fontSize: tokens.fontSize.small }}>
                color.text.{key}
              </Typography.Text>
            </Flex>
          ))}
        </Flex>
      </Example>

      <Example
        title="Background"
        description="Layer model: layout is the page, container holds content (cards, forms), subtle marks table headers and hover states. Never hard-code white."
        usage="tokens.color.background.container"
      >
        <SwatchGrid
          group="color.background."
          colors={tokens.color.background}
        />
      </Example>

      <Example
        title="Border and fill"
        description="border.default outlines controls, border.subtle separates (dividers, page frame). fill colors inactive indicators such as an empty badge."
        usage="tokens.color.border.subtle"
      >
        <Flex vertical gap={tokens.gap.medium}>
          {Object.entries(tokens.color.border).map(([key, value]) => (
            <Flex
              key={key}
              justify="space-between"
              align="center"
              style={{
                ...blockStyle,
                background: tokens.color.background.container,
                borderColor: value,
              }}
            >
              <Typography.Text>Outlined box</Typography.Text>
              <TokenLabel name={`color.border.${key}`} value={value} />
            </Flex>
          ))}
          <Flex align="center" gap={tokens.gap.medium}>
            <Badge
              count={0}
              showZero
              color={tokens.color.fill}
              styles={{ indicator: { color: tokens.color.text.disabled } }}
            />
            <TokenLabel name="color.fill" value={tokens.color.fill} />
          </Flex>
        </Flex>
      </Example>
    </Section>
  );
};

const TypographySection = () => (
  <Section title="Typography">
    <Example
      title="Font sizes"
      description="medium is the body size. large for emphasised labels (collapse headers, page descriptions), small for captions and meta information."
      usage="style={{ fontSize: tokens.fontSize.large }}"
    >
      <Flex vertical gap={tokens.gap.small}>
        {SCALE.map((size) => (
          <Flex
            key={size}
            justify="space-between"
            align="baseline"
            gap={tokens.gap.medium}
          >
            <Typography.Text style={{ fontSize: tokens.fontSize[size] }}>
              Camping trip packing list
            </Typography.Text>
            <Caption>
              fontSize.{size} · {tokens.fontSize[size]} px
            </Caption>
          </Flex>
        ))}
      </Flex>
    </Example>

    <Example
      title="Font weights"
      description="Only two weights. strong for headings and labels; emphasis on active states comes from color, not weight."
      usage="style={{ fontWeight: tokens.fontWeight.strong }}"
    >
      <Flex vertical gap={tokens.gap.small}>
        {Object.entries(tokens.fontWeight).map(([key, value]) => (
          <Flex key={key} justify="space-between" gap={tokens.gap.medium}>
            <Typography.Text style={{ fontWeight: value }}>
              Camping trip packing list
            </Typography.Text>
            <Caption>
              fontWeight.{key} · {value}
            </Caption>
          </Flex>
        ))}
      </Flex>
    </Example>

    <Example
      title="Headings"
      description="Headings use Typography.Title levels, not custom font sizes. Level 4 is the page title, level 5 the section title (TitleDivider)."
      usage="<Typography.Title level={4}>"
    >
      <Flex vertical gap={tokens.gap.small}>
        {([4, 5] as const).map((level) => (
          <Flex key={level} justify="space-between" align="baseline">
            <Typography.Title level={level} style={{ margin: 0 }}>
              Planning Menu
            </Typography.Title>
            <Caption>level {level}</Caption>
          </Flex>
        ))}
      </Flex>
    </Example>
  </Section>
);

const ShapeSection = () => (
  <Section title="Shape and elevation">
    <Example
      title="Radius"
      description="small for tags and tooltips, medium for controls (buttons, inputs), large for surfaces (panels, page frame). Nested elements never have a larger radius than their container."
      usage="style={{ borderRadius: tokens.radius.large }}"
    >
      <Flex wrap gap={tokens.gap.large}>
        {SCALE.map((size) => (
          <Flex key={size} vertical gap={tokens.gap.small}>
            <div
              style={{
                width: tokens.controlHeight.large * 2,
                height: tokens.controlHeight.large,
                background: tokens.color.background.subtle,
                border: `${tokens.borderWidth}px solid ${tokens.color.border.default}`,
                borderRadius: tokens.radius[size],
              }}
            />
            <TokenLabel
              name={`radius.${size}`}
              value={`${tokens.radius[size]} px`}
            />
          </Flex>
        ))}
      </Flex>
    </Example>

    <Example
      title="Control height"
      description="Matches antd's size prop. Use large for primary inputs and actions on forms, medium (default) elsewhere. Use it also to size elements next to controls (logo, number inputs)."
      usage='<Button size="large"> · tokens.controlHeight.large'
    >
      <Flex wrap align="end" gap={tokens.gap.large}>
        {SCALE.map((size) => (
          <Flex key={size} vertical gap={tokens.gap.small}>
            <Button size={ANTD_SIZE[size]}>Add item</Button>
            <TokenLabel
              name={`controlHeight.${size}`}
              value={`${tokens.controlHeight[size]} px`}
            />
          </Flex>
        ))}
      </Flex>
    </Example>

    <Example
      title="Border and shadow"
      description="Flat first: separate with a border before reaching for a shadow. shadow.raised for surfaces resting on the page, shadow.popup only for floating layers. Borders are always borderWidth solid."
      usage="style={{ boxShadow: tokens.shadow.raised }}"
    >
      <Row gutter={[tokens.gap.medium, tokens.gap.medium]}>
        {[
          { name: "borderWidth", style: {} },
          { name: "shadow.raised", style: { boxShadow: tokens.shadow.raised } },
          { name: "shadow.popup", style: { boxShadow: tokens.shadow.popup } },
        ].map(({ name, style }) => (
          <Col key={name} xs={24} sm={8}>
            <div
              style={{
                padding: tokens.padding.medium,
                border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
                borderRadius: tokens.radius.large,
                background: tokens.color.background.container,
                ...style,
              }}
            >
              <Typography.Text code style={{ fontSize: tokens.fontSize.small }}>
                {name}
              </Typography.Text>
            </div>
          </Col>
        ))}
      </Row>
    </Example>
  </Section>
);

export const DocsPage = () => (
  <Flex vertical style={{ paddingBottom: tokens.padding.large }}>
    <SectionHeader
      title="Design Tokens"
      extra={<DocsNav />}
      description="Living reference for docs/design-tokens.md. Every example reads its values from src/theme/tokens.ts."
    />

    <Flex vertical gap={tokens.gap.small}>
      <Typography.Text strong>Principles</Typography.Text>
      <ol style={{ margin: 0, paddingInlineStart: tokens.padding.large }}>
        <li>
          One source: every visual value comes from tokens; hex values only for
          one-off problems.
        </li>
        <li>No antd tokens (theme.useToken, --ant-*) in components.</li>
        <li>Three sizes: small / medium / large.</li>
        <li>Role over value: pick a token by its purpose.</li>
        <li>4 px grid for all spacing.</li>
      </ol>
    </Flex>

    <SpacingSection />
    <LayoutSection />
    <ColorSection />
    <TypographySection />
    <ShapeSection />
  </Flex>
);
