import {
  AutoComplete,
  Button,
  Checkbox,
  Collapse,
  Flex,
  Form,
  type FormInstance,
  Input,
  InputNumber,
  Select,
  Switch,
  Typography,
} from "antd";
import { useMemo, useState } from "react";

import { useApi } from "#/api/apiContext.ts";
import { toSelectOptions } from "#/common/components/input/SelectOption.ts";
import {
  CUISINE_OPTIONS,
  DIET_OPTIONS,
  LANGUAGE_OPTIONS,
  RECIPE_PROPERTY_OPTIONS,
} from "#/common/enumOptions.ts";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { normalizeString } from "#/common/normalizeString.ts";
import { type IngredientUiDTO, Language } from "#/generated/interface-api.ts";
import { DIFFICULTY_OPTIONS } from "#/page/recipe_creation/utils/recipeMapper.ts";
import {
  RECIPE_SEARCH_FORM_VALUES_FIELDS,
  type RecipeSearchFormValues,
} from "#/page/recipe_search/utils/recipeSearchFormUtils.ts";
import { tokens } from "#/theme/tokens.ts";
import { useDebouncedValue } from "#/common/hooks/useDebouncedValue.ts";
import { useFetch } from "#/common/hooks/useFetch.ts";

export type RecipeSearchBoxProps = {
  form: FormInstance<RecipeSearchFormValues>;
  onSearch: (values: RecipeSearchFormValues) => void;
  searching: boolean;
};

export const RecipeSearchBox = ({
  form,
  onSearch,
  searching,
}: RecipeSearchBoxProps) => {
  const { ingredient, recipe } = useApi();
  const [showAdvanced, setShowAdvanced] = useState<boolean>(false);
  const filterByFolder: boolean | undefined = Form.useWatch(
    RECIPE_SEARCH_FORM_VALUES_FIELDS.filterByFolder,
    form,
  );

  const titleFilter: string | undefined = Form.useWatch(
    RECIPE_SEARCH_FORM_VALUES_FIELDS.title,
    form,
  );
  const debouncedTitleFilter = useDebouncedValue(titleFilter, 300);
  const [recipeTitles] = useFetch(
    () =>
      recipe.getRecipeTitles({
        titlePrefix: debouncedTitleFilter ?? "",
        considerOwner: false,
      }),
    [debouncedTitleFilter],
  );
  const titleOptions = useMemo(
    () =>
      toSelectOptions(
        recipeTitles ?? [],
        (title) => title,
        (title) => title,
      ),
    [recipeTitles],
  );

  const [ingredients, ingredientsLoading] = useFetchOnce(() =>
    ingredient.getAllStandingDataIngredients({
      language: Language.DE,
    }),
  );
  const ingredientsByNormalizedName = useMemo(
    () =>
      new Map(
        (ingredients ?? []).map((ingredient) => [
          normalizeString(ingredient.name),
          ingredient,
        ]),
      ),
    [ingredients],
  );
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
    <Collapse
      size={"large"}
      defaultActiveKey={["searchOptions"]}
      items={[
        {
          key: "searchOptions",
          label: (
            <Typography.Text strong style={{ fontSize: tokens.fontSize.large }}>
              Suchoptionen
            </Typography.Text>
          ),
          children: (
            <Form
              form={form}
              layout={"vertical"}
              initialValues={
                {
                  language: Language.EN, // TODO LGE/FDA: Retrieve language from the context
                  ingredients: [],
                  cuisines: [],
                  properties: [],
                } satisfies RecipeSearchFormValues
              }
              onFinish={onSearch}
            >
              <Flex vertical gap={tokens.gap.small}>
                <Flex gap={tokens.gap.small}>
                  <Form.Item
                    name={RECIPE_SEARCH_FORM_VALUES_FIELDS.title}
                    style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                  >
                    <AutoComplete
                      allowClear
                      placeholder={"Title"}
                      options={titleOptions}
                      showSearch={{
                        filterOption: (input, option) =>
                          normalizeString(option?.value ?? "").includes(
                            normalizeString(input),
                          ),
                      }}
                    />
                  </Form.Item>
                  <Form.Item
                    name={RECIPE_SEARCH_FORM_VALUES_FIELDS.language}
                    style={{ marginBottom: 0 }}
                  >
                    <Select
                      placeholder={"Language"}
                      options={LANGUAGE_OPTIONS}
                      style={{ width: tokens.width.selectField }}
                    />
                  </Form.Item>
                </Flex>

                <Form.Item
                  name={RECIPE_SEARCH_FORM_VALUES_FIELDS.ingredients}
                  style={{ marginBottom: 0 }}
                  getValueProps={(selected: IngredientUiDTO[] = []) => ({
                    value: selected.map(({ name }) => normalizeString(name)),
                  })}
                  getValueFromEvent={(values: string[]): IngredientUiDTO[] =>
                    values.flatMap(
                      (value) => ingredientsByNormalizedName.get(value) ?? [],
                    )
                  }
                >
                  <Select
                    mode={"multiple"}
                    allowClear
                    placeholder={"Ingredients"}
                    options={ingredientOptions}
                    loading={ingredientsLoading}
                    showSearch={{ optionFilterProp: "label" }}
                  />
                </Form.Item>

                <Flex
                  component={"label"}
                  align={"center"}
                  gap={tokens.gap.small}
                >
                  <Switch checked={showAdvanced} onChange={setShowAdvanced} />
                  <Typography.Text>Show advanced</Typography.Text>
                </Flex>

                {showAdvanced && (
                  <>
                    <Flex gap={tokens.gap.small}>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.diet}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <Select
                          allowClear
                          placeholder={"Diet"}
                          options={DIET_OPTIONS}
                        />
                      </Form.Item>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.cookingTime}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <InputNumber
                          placeholder={"Kochdauer"}
                          min={0}
                          style={{ width: "100%" }}
                        />
                      </Form.Item>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.difficulty}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <Select
                          allowClear
                          placeholder={"Difficulty"}
                          options={DIFFICULTY_OPTIONS}
                        />
                      </Form.Item>
                    </Flex>

                    <Flex gap={tokens.gap.small}>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.cuisines}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <Select
                          mode={"multiple"}
                          allowClear
                          placeholder={"Cuisine"}
                          options={CUISINE_OPTIONS}
                        />
                      </Form.Item>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.properties}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <Select
                          mode={"multiple"}
                          allowClear
                          placeholder={"Properties"}
                          options={RECIPE_PROPERTY_OPTIONS}
                        />
                      </Form.Item>
                    </Flex>

                    <Flex align={"center"} gap={tokens.gap.small}>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.filterByFolder}
                        valuePropName={"checked"}
                        style={{ marginBottom: 0 }}
                      >
                        <Checkbox>Filter Folder</Checkbox>
                      </Form.Item>
                      <Form.Item
                        name={RECIPE_SEARCH_FORM_VALUES_FIELDS.folderName}
                        style={{ flex: 1, minWidth: 0, marginBottom: 0 }}
                      >
                        <Input // TODO LGE/JPI: Load folderNames in Select
                          placeholder={"Folder"}
                          disabled={!filterByFolder}
                        />
                      </Form.Item>
                    </Flex>
                  </>
                )}

                <Button
                  type={"primary"}
                  htmlType={"submit"}
                  block
                  loading={searching}
                >
                  Suchen
                </Button>
              </Flex>
            </Form>
          ),
        },
      ]}
    />
  );
};
