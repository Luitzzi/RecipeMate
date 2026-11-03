import {Checkbox, type CheckboxProps, Collapse, Flex, Typography} from "antd";
import {useState} from "react";

import type {RecipeUiDTO} from "#/generated/interface-api.ts";
import {tokens} from "#/theme/tokens.ts";
import {UNIT_LABELS} from "#/common/enumOptions.ts";

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
		<Collapse
			size="large"
			items={[
				{
					key: "ingredients",
					label: recipe.title,
					extra: (
						<Checkbox
							indeterminate={indeterminate}
							checked={checkAll}
							onChange={onCheckAllChange}
							// Toggling all ingredients must not expand the panel
							onClick={(event) => event.stopPropagation()}
						>
							{`${checkedList.length}/${allIndices.length}`}
						</Checkbox>
					),
					children: (
						<Checkbox.Group
							value={checkedList}
							onChange={setCheckedList}
							style={{width: "100%"}}
						>
							<Flex vertical style={{width: "100%"}}>
								{recipe.ingredients.map((item, index) => (
									<Checkbox
										key={index}
										value={index}
										// Full-width rows with control height for easy tapping
										style={{
											minHeight: tokens.controlHeight.large,
											alignItems: "center",
										}}
									>
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
					),
				},
			]}
		/>
	);
}
