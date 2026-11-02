import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import {
  Button,
  Flex,
  Form,
  type FormInstance,
  InputNumber,
  Select,
} from "antd";
import { useMemo } from "react";

import { AddableSelect } from "#/common/components/input/AddableSelect.tsx";
import { toSelectOptions } from "#/common/components/input/SelectOption.ts";
import { UNIT_OPTIONS } from "#/common/enumOptions.ts";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { normalizeString } from "#/common/normalizeString.ts";
import {
  IngredientUiControllerClient,
  type IngredientUiDTO,
  Language,
} from "#/generated/interface-api.ts";
import {
  getEmptyIngredient,
  INGREDIENT_VALUES_FIELDS,
  type IngredientFormValues,
  RECIPE_CREATION_FORM_FIELDS,
  type RecipeCreationFormValues,
} from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import { tokens } from "#/theme/tokens.ts";

export type IngredientSelectProps = {
  form: FormInstance<RecipeCreationFormValues>;
  ingredientClient: IngredientUiControllerClient;
};

export const IngredientSelectionList = ({
  form,
  ingredientClient,
}: IngredientSelectProps) => {
  const [ingredients, ingredientsLoading] = useFetchOnce(() =>
    ingredientClient.getAllStandingDataAndCustomIngredients({
      language: Language.DE,
    }),
  );
  const formIngredients: IngredientFormValues[] | undefined = Form.useWatch(
    RECIPE_CREATION_FORM_FIELDS.ingredients,
    form,
  );
  // Includes ingredients created in the form, so they are selectable in other rows
  const ingredientsByNormalizedName = useMemo(() => {
    const createdIngredients = (formIngredients ?? [])
      .map((value) => value?.ingredient)
      .filter(
        (ingredient): ingredient is IngredientUiDTO =>
          ingredient !== undefined && ingredient.name !== undefined,
      );
    return new Map(
      [...(ingredients ?? []), ...createdIngredients].map((ingredient) => [
        normalizeString(ingredient.name),
        ingredient,
      ]),
    );
  }, [ingredients, formIngredients]);
  const ingredientOptions = useMemo(
    () =>
      toSelectOptions(
        ingredients ?? [],
        (ingredient) => normalizeString(ingredient.name),
        (ingredient) => ingredient.name,
      ),
    [ingredients],
  );

  return (
    <>
      <Form.List name={RECIPE_CREATION_FORM_FIELDS.ingredients}>
        {(fields, { add, remove }) => (
          <Flex vertical gap={tokens.gap.small}>
            {fields.map(({ key, name }) => (
              <Flex key={key} gap={tokens.gap.small} align={"start"}>
                <Form.Item
                  name={[name, INGREDIENT_VALUES_FIELDS.ingredient]}
                  rules={[{ required: true, message: "Select an ingredient" }]}
                  style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                  getValueProps={(ingredient?: IngredientUiDTO) => ({
                    value:
                      ingredient !== undefined && ingredient.name
                        ? normalizeString(ingredient.name)
                        : undefined,
                  })}
                  getValueFromEvent={(
                    value?: string,
                  ): IngredientUiDTO | undefined =>
                    value !== undefined
                      ? (ingredientsByNormalizedName.get(
                          normalizeString(value),
                        ) ?? { name: value.trim() })
                      : undefined
                  }
                >
                  <AddableSelect
                    placeholder={"Ingredient"}
                    selectOptions={ingredientOptions}
                    loading={ingredientsLoading}
                  />
                </Form.Item>
                <Form.Item
                  name={[name, INGREDIENT_VALUES_FIELDS.amount]}
                  style={{ marginBottom: 0 }}
                >
                  <InputNumber
                    placeholder={"Amount"}
                    min={0}
                    style={{ width: tokens.width.numberField }}
                  />
                </Form.Item>
                <Form.Item
                  name={[name, INGREDIENT_VALUES_FIELDS.unit]}
                  rules={[{ required: true, message: "Select a unit" }]}
                  style={{ marginBottom: 0 }}
                >
                  <Select
                    placeholder={"Unit"}
                    options={UNIT_OPTIONS}
                    style={{ width: tokens.width.selectField }}
                  />
                </Form.Item>

                <Button
                  type="text"
                  icon={<DeleteOutlined />}
                  onClick={() => remove(name)}
                />
              </Flex>
            ))}
            <Button
              type="dashed"
              block
              icon={<PlusOutlined />}
              onClick={() => add(getEmptyIngredient())}
            >
              Add ingredient
            </Button>
          </Flex>
        )}
      </Form.List>
    </>
  );
};
