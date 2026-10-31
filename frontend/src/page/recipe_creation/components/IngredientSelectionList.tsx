import {
  Button,
  Flex,
  Form,
  type FormInstance,
  InputNumber,
  Select,
} from "antd";
import {
  getEmptyIngredient,
  INGREDIENT_VALUES_FIELDS,
  type IngredientFormValues,
  RECIPE_CREATION_FORM_FIELDS,
  type RecipeCreationFormValues,
} from "../utils/recipeCreationFormUtils";
import {
  IngredientUiControllerClient,
  type IngredientUiDTO,
  Language,
} from "#/generated/interface-api.ts";
import { useMemo } from "react";
import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { toSelectOptions } from "#/common/components/input/SelectOption.ts";
import { AddableSelect } from "#/common/components/input/AddableSelect.tsx";
import { tokens } from "#/theme/tokens.ts";
import { UNIT_OPTIONS } from "#/common/enumOptions.ts";
import { normalizeString } from "#/common/normalizeString.ts";

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
                    size={"large"}
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
                    size={"large"}
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
                    size={"large"}
                    placeholder={"Unit"}
                    options={UNIT_OPTIONS}
                    style={{ width: tokens.width.selectField }}
                  />
                </Form.Item>

                <Button
                  type="text"
                  size="large"
                  icon={<DeleteOutlined />}
                  onClick={() => remove(name)}
                />
              </Flex>
            ))}
            <Button
              type="dashed"
              size="large"
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
