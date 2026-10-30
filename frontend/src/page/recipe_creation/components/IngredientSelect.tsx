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
  type IngredientValues,
  normalizeName,
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

export type IngredientSelectProps = {
  form: FormInstance<RecipeCreationFormValues>;
  ingredientClient: IngredientUiControllerClient;
};

export const IngredientSelect = ({
  form,
  ingredientClient,
}: IngredientSelectProps) => {
  const [ingredients, ingredientsLoading] = useFetchOnce(() =>
    ingredientClient.getAllStandingDataAndCustomIngredients({
      language: Language.DE,
    }),
  );
  const formIngredients: IngredientValues[] | undefined = Form.useWatch(
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
        normalizeName(ingredient.name),
        ingredient,
      ]),
    );
  }, [ingredients, formIngredients]);
  const ingredientOptions = useMemo(
    () =>
      toSelectOptions(
        ingredients ?? [],
        (ingredient) => normalizeName(ingredient.name),
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
                        ? normalizeName(ingredient.name)
                        : undefined,
                  })}
                  getValueFromEvent={(
                    value?: string,
                  ): IngredientUiDTO | undefined =>
                    value !== undefined
                      ? (ingredientsByNormalizedName.get(
                          normalizeName(value),
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
                <Form.Item name={[name, INGREDIENT_VALUES_FIELDS.amount]}>
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
                  aria-label="Remove item"
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
