import {Card, Checkbox, type CheckboxProps, Flex, Typography} from "antd";
import {useState} from "react";

import {UNIT_LABELS} from "#/common/enumOptions.ts";
import type {RecipeUiDTO} from "#/generated/interface-api.ts";
import {tokens} from "#/theme/tokens.ts";

type Props = {
	recipe: RecipeUiDTO;
};

export default function MealDrawerRecipe({recipe}: Props) {
	// Indices instead of names: a recipe can list the same ingredient twice
	const allIndices = recipe.ingredients.map((_, index) => index);
	const [checkedList, setCheckedList] = useState<number[]>(allIndices);

	const checkAll = checkedList.length === allIndices.length;
	const indeterminate = checkedList.length > 0 && !checkAll;

	const onCheckAllChange: CheckboxProps["onChange"] = (event) =>
		setCheckedList(event.target.checked ? allIndices : []);

	return (
		<Card
			size="small"
			title={
				<Checkbox
					indeterminate={indeterminate}
					checked={checkAll}
					onChange={onCheckAllChange}
				>
					{recipe.title}
				</Checkbox>
			}
		>
			<Checkbox.Group
				value={checkedList}
				onChange={setCheckedList}
				style={{width: "100%"}}
			>
				<Flex vertical gap={tokens.gap.small}>
					{recipe.ingredients.map((item, index) => (
						<Checkbox key={index} value={index}>
							{item.ingredient.name}
							{item.amount != null && (
								<Typography.Text type="secondary">
									{` · ${item.amount} ${UNIT_LABELS[item.unit]}`}
								</Typography.Text>
							)}
						</Checkbox>
					))}
				</Flex>
			</Checkbox.Group>
		</Card>
	);
}
