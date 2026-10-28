import {
	HomeFilled,
	InfoCircleOutlined, ShoppingOutlined,
	SnippetsOutlined, UserOutlined,
} from "@ant-design/icons";
import {Avatar, Flex, Grid, Layout, Menu} from "antd";
import {Link, Outlet, useLocation, useNavigate} from "react-router";
import {tokens} from "#/theme/tokens.ts";

const {Content} = Layout;

const NAV_ITEMS = [
	{key: "/shopping-list", icon: <ShoppingOutlined />, label: "Shopping List"},
	{key: "/meal-prepping", icon: <ShoppingOutlined />, label: "Meal Prepping"},
	{key: "/", icon: <HomeFilled/>, label: "Home"},
	{key: "/profile", icon: <UserOutlined />, label: "Profile"},
	{key: "/meal-prepping", icon: <UserOutlined />, label: "Meal Prepping"},
];

const selectedKeyFor = (pathname: string) =>
	NAV_ITEMS.find(({key}) =>
		key === "/" ? pathname === "/" : pathname.startsWith(key),
	)?.key ?? pathname;

const MobileTabBar = ({selectedKey}: { selectedKey: string }) => (
	<Flex
		component="nav"
		style={{
			position: "sticky",
			bottom: 0,
			background: tokens.color.background.container,
			borderTop: `${tokens.borderWidth}px solid ${tokens.color.border.subtle}`,
		}}
	>
		{NAV_ITEMS.map(({key, icon, label}) => {
			const active = key === selectedKey;
			return (
				<Link
					key={key}
					to={key}
					aria-current={active ? "page" : undefined}
					style={{
						flex: 1,
						height: '85px',
						display: "flex",
						flexDirection: "column",
						fontSize: "16pt",
						alignItems: "center",
						justifyContent: 'center',
						gap: tokens.gap.small,
						color: active ? tokens.color.primary : tokens.color.text.secondary,
						fontWeight: active
							? tokens.fontWeight.strong
							: tokens.fontWeight.regular,
						textDecoration: "none",
						overflow: 'hidden'
					}}>
					{icon}
				</Link>
			);
		})}
	</Flex>
);

export const RootLayout = () => {
	const {pathname} = useLocation();
	const navigate = useNavigate();
	const screens = Grid.useBreakpoint();
	const selectedKey = selectedKeyFor(pathname);

	// Framed white box on larger screens, edge to edge on phones.
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
				{framed && (
					<Menu
						mode="horizontal"
						items={NAV_ITEMS}
						selectedKeys={[selectedKey]}
						onClick={({key}) => navigate(key)}
					/>
				)}

				<Content
					style={{
						flex: 1,
						padding: `${gutter}px ${gutter}px ${framed ? 0 : gutter}px`,
					}}
				>
					<Outlet/>
				</Content>

				{!framed && <MobileTabBar selectedKey={selectedKey}/>}
			</Flex>
		</Layout>
	);
};
