import {Flex, Grid, Layout} from "antd";
import {Outlet} from "react-router";
import {tokens} from "#/theme/tokens.ts";
import {Navigation} from "#/common/components/Navigation.tsx";

const {Content} = Layout;

export const RootLayout = () => {
	const screens = Grid.useBreakpoint();

	// Framed white box on larger screens, edge to edge on phones.
	const framed = !!screens.md;
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
				{framed && <Navigation framed={framed}/>}

				<Content
					style={{
						flex: 1,
						padding: `${gutter}px ${gutter}px ${framed ? 0 : gutter}px`,
					}}
				>
					<Outlet/>
				</Content>

				{!framed && <Navigation framed={framed}/>}
			</Flex>
		</Layout>
	);
};
