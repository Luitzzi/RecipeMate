import { Button, Form, Select, Skeleton } from "antd";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useApi } from "#/api/apiContext.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import {
  ALLERGENS,
  COOKING_SKILLS,
  CUISINES,
  DIETS,
  PREFERENCES_FORM_FIELDS,
  type PreferencesFormValues,
  toApiLanguage,
  toFormValues,
  toUserUiDTO,
} from "#/page/profile/utils/preferencesFormUtils.ts";

export const PreferencesForm = () => {
  const [form] = Form.useForm<PreferencesFormValues>();
  const api = useApi();
  const notify = useNotification();
  const { t, i18n } = useTranslation();
  const language = toApiLanguage(i18n.resolvedLanguage);

  const [preferences, loading, loadError] = useFetchOnce(() =>
    api.user.getPreferences({ language }),
  );

  const [ingredients, ingredientsLoading] = useFetchOnce(() =>
    api.ingredient.getAllStandingDataIngredients({ language }),
  );

  const [save, , saving, saveError] = useMutation(
    (values: PreferencesFormValues) =>
      api.user.updatePreferences(toUserUiDTO(values, ingredients ?? []), {
        language,
      }),
  );

  useEffect(() => {
    if (preferences) form.setFieldsValue(toFormValues(preferences));
  }, [preferences, form]);

  useEffect(() => {
    if (loadError) notify.error({ title: t("profile.preferences.loadFailed") });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loadError]);

  useEffect(() => {
    if (saveError)
      notify.error({
        title: t("profile.preferences.saveFailed"),
        description: saveError.message,
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [saveError]);

  const onFinish = async (values: PreferencesFormValues) => {
    if (await save(values))
      notify.success({ title: t("profile.preferences.saved") });
  };

  const options = <T extends string>(values: T[], group: string) =>
    values.map((value) => ({ value, label: t(`enums.${group}.${value}`) }));

  if (loading) return <Skeleton active />;

  return (
    <Form
      form={form}
      layout={"vertical"}
      onFinish={onFinish}
      disabled={!preferences || !ingredients}
    >
      <SectionHeader title={t("profile.preferences.title")} />
      <Form.Item
        name={PREFERENCES_FORM_FIELDS.diet}
        label={t("profile.preferences.diet")}
        rules={[{ required: true }]}
      >
        <Select options={options(DIETS, "diet")} />
      </Form.Item>
      <Form.Item
        name={PREFERENCES_FORM_FIELDS.cookingSkill}
        label={t("profile.preferences.cookingSkill")}
      >
        <Select allowClear options={options(COOKING_SKILLS, "cookingSkill")} />
      </Form.Item>
      <Form.Item
        name={PREFERENCES_FORM_FIELDS.preferredCuisines}
        label={t("profile.preferences.preferredCuisines")}
      >
        <Select mode={"multiple"} options={options(CUISINES, "cuisine")} />
      </Form.Item>
      <Form.Item
        name={PREFERENCES_FORM_FIELDS.allergens}
        label={t("profile.preferences.allergens")}
      >
        <Select mode={"multiple"} options={options(ALLERGENS, "allergen")} />
      </Form.Item>
      <Form.Item
        name={PREFERENCES_FORM_FIELDS.dislikedIngredientIds}
        label={t("profile.preferences.dislikedIngredients")}
      >
        <Select
          mode={"multiple"}
          showSearch
          optionFilterProp={"label"}
          loading={ingredientsLoading}
          options={ingredients?.map((ingredient) => ({
            value: ingredient.id,
            label: ingredient.name,
          }))}
        />
      </Form.Item>
      <Button type={"primary"} htmlType={"submit"} loading={saving}>
        {t("buttons.save")}
      </Button>
    </Form>
  );
};
