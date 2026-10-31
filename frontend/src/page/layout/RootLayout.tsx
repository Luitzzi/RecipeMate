import {
  InfoCircleOutlined,
  SnippetsOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Avatar, Flex, Grid, Layout, Menu } from "antd";
import { Link, Outlet, useLocation, useNavigate } from "react-router";
import logo from "../../assets/holidaymate-logo.png";
import { tokens } from "#/theme/tokens.ts";
import { AiChat } from "#/common/components/ai_chat/AiChat.tsx";
import useRouteHandle from "#/common/hooks/useRouteHandle.ts";

const { Header, Content } = Layout;

const NAV_ITEMS = [
  { key: "/docs", icon: <SnippetsOutlined />, label: "Documentation" },
  { key: "/test", icon: <InfoCircleOutlined />, label: "Test" },
];

export const RootLayout = () => {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const screens = Grid.useBreakpoint();
  const aiChatEnabled = useRouteHandle("aiChat");
  // Icon-only navigation on phones, so it fits next to the logo.
  const navItems = screens.sm
    ? NAV_ITEMS
    : NAV_ITEMS.map(({ label, ...item }) => ({ ...item, title: label }));

  // Framed white box on larger screens (see docs/design-*.png), edge to edge on phones.
  const framed = screens.md;
  const gutter = framed
    ? tokens.layout.gutter.desktop
    : tokens.layout.gutter.mobile;

  return (
    <Layout
      style={{
        minHeight: "100vh",
        background: tokens.color.background.layout,
      }}
    >
      <Flex
        vertical
        style={{
          width: "100%",
          maxWidth: tokens.layout.maxWidth,
          minHeight: framed
            ? `calc(100vh - ${2 * tokens.layout.frameMargin}px)`
            : "100vh",
          margin: framed ? `${tokens.layout.frameMargin}px auto` : 0,
          background: tokens.color.background.container,
          ...(framed && {
            border: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
            borderRadius: tokens.radius.large,
            boxShadow: tokens.shadow.raised,
            // clip instead of hidden keeps position: sticky working inside the box
            overflow: "clip",
          }),
        }}
      >
        <Header
          style={{
            display: "flex",
            alignItems: "center",
            gap: tokens.gap.large,
            padding: `0 ${gutter}px`,
            background: tokens.color.background.container,
            borderBottom: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
          }}
        >
          <Link to="/" style={{ display: "flex", flexShrink: 0 }}>
            <img
              src={logo}
              alt="HolidayMate"
              style={{
                height: tokens.controlHeight.large,
                display: "block",
              }}
            />
          </Link>
          <Menu
            mode="horizontal"
            items={navItems}
            selectedKeys={[pathname]}
            onClick={({ key }) => navigate(key)}
            style={{ flex: 1, minWidth: 0, borderBottom: "none" }}
          />
          <Avatar size="large" icon={<UserOutlined />} />
        </Header>
        <Content style={{ padding: `${gutter}px ${gutter}px 0` }}>
          <Outlet />
        </Content>
      </Flex>
      {aiChatEnabled && <AiChat />}
    </Layout>
  );
};
