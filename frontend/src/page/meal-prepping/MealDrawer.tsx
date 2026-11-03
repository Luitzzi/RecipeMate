import {ShoppingCartOutlined} from "@ant-design/icons";
import {Button, Drawer, Empty, Flex} from "antd";
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
				placement="bottom"
				onClose={() => setOpen(false)}
				open={open}
			>
				{recipes.length === 0 ? (
					<Empty description={t("noRecipesPlanned")}/>
				) : (
					<Flex vertical gap={tokens.gap.medium}>
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
