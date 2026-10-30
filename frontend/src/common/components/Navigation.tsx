import {Flex, Menu} from "antd";
import {Link, useLocation, useNavigate} from "react-router";
import {tokens} from "#/theme/tokens.ts";
import {CookingPot, Home, List, ShoppingCart, User} from "lucide-react";
import type {LucideIcon} from "lucide-react";

// Antd's horizontal Menu icon slot is tuned for ~16px icons; the mobile tab
// bar has room for lucide's own default size.
const DESKTOP_ICON_SIZE = 16;
const MOBILE_ICON_SIZE = 24;

const NAV_ITEMS: {key: string; icon: LucideIcon; label: string}[] = [
	{key: "/shopping-list", icon: ShoppingCart, label: "Shopping List"},
	{key: "/meal-prepping", icon: CookingPot, label: "Meal Prepping"},
	{key: "/", icon: Home, label: "Feed"},
    {key: "/recipe", icon: User, label: "Meine Rezepte"},
    {key: "/profile", icon: List, label: "Profile"},
];

const selectedKeyFor = (pathname: string) =>
	NAV_ITEMS.find(({key}) =>
		key === "/" ? pathname === "/" : pathname.startsWith(key),
	)?.key ?? pathname;

const menuItems = NAV_ITEMS.map(({key, icon: Icon, label}) => ({
	key,
	icon: <Icon size={DESKTOP_ICON_SIZE}/>,
	label,
}));

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
		{NAV_ITEMS.map(({key, icon: Icon, label}) => {
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
					<Icon size={MOBILE_ICON_SIZE}/>
				</Link>
			);
		})}
	</Flex>
);

type NavigationProps = {
	/** Horizontal menu on framed (desktop) layouts, sticky tab bar otherwise. */
	framed: boolean;
};

export const Navigation = ({framed}: NavigationProps) => {
	const {pathname} = useLocation();
	const navigate = useNavigate();
	const selectedKey = selectedKeyFor(pathname);

	if (framed) {
		return (
			<Menu
				mode="horizontal"
				items={menuItems}
				selectedKeys={[selectedKey]}
				onClick={({key}) => navigate(key)}
			/>
		);
	}

	return <MobileTabBar selectedKey={selectedKey}/>;
};
