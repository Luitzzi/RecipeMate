import {PlusOutlined} from "@ant-design/icons";
import {Button, Card, Flex, Tag} from "antd";
import dayjs, {type Dayjs} from "dayjs";
import {useTranslation} from "react-i18next";
import type {
	MealPlanDayUiDTO,
	RecipeUiDTO,
} from "#/generated/interface-api.ts";
import {tokens} from "#/theme/tokens.ts";
import {RecipePickerPopover} from "./RecipePickerPopover.tsx";
import MealDrawer from "#/page/meal-prepping/MealDrawer.tsx";

type MealPlanDayCardProps = {
	date: Dayjs;
	day: MealPlanDayUiDTO;
	recipes: RecipeUiDTO[];
	recipesById: Map<number, RecipeUiDTO>;
	onChange: (day: MealPlanDayUiDTO) => void;
};

export const MealPlanDayCard = (props: MealPlanDayCardProps) => {
	const {date, day, recipes, recipesById, onChange} = props;

	const {t} = useTranslation("mealPrepping");
	const isToday = date.isSame(dayjs(), "day");

	const removeRecipe = (index: number) =>
		onChange({
			...day,
			recipeIds: day.recipeIds.filter((_, i) => i !== index),
		});

	return (
		<Card
			size="small"
			title={
				<Flex align="center" gap={tokens.gap.small}>
					{date.format("dddd, D. MMMM")}
					{isToday && <Tag color="blue">{t("today")}</Tag>}
				</Flex>
			}
			style={{
				height: "100%",
				borderColor: isToday ? tokens.color.primary : undefined,
			}}
		>
			<Flex vertical gap={tokens.gap.medium}>
				{day.recipeIds.length > 0 && (
					<Flex wrap gap={tokens.gap.small}>
						{day.recipeIds.map((recipeId, index) => (
							<Tag
								// The same recipe can be planned multiple times a day
								key={`${recipeId}-${index}`}
								closable
								color="geekblue"
								style={{margin: 0}}
								onClose={(event) => {
									event.preventDefault();
									removeRecipe(index);
								}}
							>
								{recipesById.get(recipeId)?.title ?? "…"}
							</Tag>
						))}
					</Flex>
				)}
				<RecipePickerPopover
					recipes={recipes}
					onPick={(recipe) =>
						onChange({
							...day,
							recipeIds: [...day.recipeIds, recipe.id as number],
						})
					}
				>
					<Button type="dashed" size="large" block icon={<PlusOutlined/>}>
						{t("addRecipe")}
					</Button>
				</RecipePickerPopover>

				{/* CTA */}
				<MealDrawer
					recipes={day.recipeIds
						.map((recipeId) => recipesById.get(recipeId))
						.filter((recipe) => recipe !== undefined)}
				/>
			</Flex>
		</Card>
	);
};
