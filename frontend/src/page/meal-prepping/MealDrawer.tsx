import {ShoppingCartOutlined} from "@ant-design/icons";
import {Button, Drawer, Empty, Flex, Grid} from "antd";
import {Fragment, useState} from "react";
import {useTranslation} from "react-i18next";

import type {RecipeUiDTO} from "#/generated/interface-api.ts";
import MealDrawerRecipe from "#/page/meal-prepping/MealDrawerRecipe.tsx";
import {tokens} from "#/theme/tokens.ts";

type Props = {
	recipes: RecipeUiDTO[];
};

export default function MealDrawer({recipes}: Props) {
	const {t} = useTranslation("mealPrepping");
	const [open, setOpen] = useState(false);
	const screens = Grid.useBreakpoint();

	// Bottom sheet on phones, side panel on larger screens.
	const mobile = !screens.md;

	const close = () => setOpen(false);

	return (
		<Fragment>
			<Button
				size="large"
				block
				icon={<ShoppingCartOutlined/>}
				onClick={() => setOpen(true)}
			>
				{t("goShopping")}
			</Button>

			<Drawer
				title={t("shoppingList")}
				placement={mobile ? "bottom" : "right"}
				size={mobile ? "85%" : "default"}
				closable={{placement: "end"}}
				onClose={close}
				open={open}
				styles={{
					body: {
						padding: mobile ? tokens.layout.gutter.mobile : tokens.padding.large,
					},
				}}
				// Thumb-reachable close action on phones
				footer={
					mobile && (
						<Button type="primary" size="large" block onClick={close}>
							{t("done")}
						</Button>
					)
				}
			>
				{recipes.length === 0 ? (
					<Empty description={t("noRecipesPlanned")}/>
				) : (
					<Flex vertical gap={tokens.gap.small}>
						{recipes.map((recipe, index) => (
							// The same recipe can be planned multiple times a day
							<MealDrawerRecipe key={`${recipe.id}-${index}`} recipe={recipe}/>
						))}
					</Flex>
				)}
			</Drawer>
		</Fragment>
	);
}
