import {
  Button,
  Flex,
  Form,
  Input,
  Upload,
  type UploadFile,
  type UploadProps,
} from "antd";
import { useEffect } from "react";
import {
  RECIPE_CREATION_FORM_FIELDS,
  type RecipeCreationFormValues,
} from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import { useApi } from "#/api/apiContext.ts";
import { UploadOutlined } from "@ant-design/icons";
import { putFile } from "#/api/putFile.ts";
import { IngredientSelectionList } from "#/page/recipe_creation/components/IngredientSelectionList.tsx";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { StepsList } from "#/page/recipe_creation/components/StepsList.tsx";
import {
  ALLOWED_IMAGE_TYPES,
  MAX_IMAGE_AMOUNT,
  validateImage,
} from "#/page/recipe_creation/utils/imageUploadUtils.ts";
import { toRecipeUiDTO } from "#/page/recipe_creation/utils/recipeMapper.ts";
import { tokens } from "#/theme/tokens.ts";

export const RecipeCreationForm = () => {
  const [form] = Form.useForm<RecipeCreationFormValues>();
  const { ingredient, recipe, image } = useApi();
  const notify = useNotification();

  const images: UploadFile[] =
    Form.useWatch(RECIPE_CREATION_FORM_FIELDS.images, form) ?? [];
  // Derived from the file list, because the mutation state is shared by parallel uploads
  const isUploading = images.some((image) => image.status === "uploading");

  const [uploadImage, , , uploadError] = useMutation(async (file: File) => {
    const { key, url } = await image.getUploadUrl({
      contentType: file.type,
    });
    await putFile(url, file);
    return key;
  });

  useEffect(() => {
    if (uploadError)
      notify.error({
        title: "Upload fehlgeschlagen",
        description: uploadError.message,
      });
  }, [notify, uploadError]);

  const beforeImageUpload: UploadProps["beforeUpload"] = (file) => {
    const validationError = validateImage(file);
    if (!validationError) return true;

    notify.error({ title: validationError });
    return Upload.LIST_IGNORE;
  };

  const customImageUploadRequest: UploadProps["customRequest"] = async ({
    file,
    onSuccess,
    onError,
  }) => {
    const key = await uploadImage(file as File);
    if (key) onSuccess?.({ key });
    else onError?.(new Error("Upload failed"));
  };

  const saveRecipe = () => {
    const formValues = form.getFieldsValue(true);
    const recipeToCreate = toRecipeUiDTO(formValues);
    const createdRecipe = recipe.create(recipeToCreate);
    // TODO LGE/OSA: Add recipe overview navigation
  };

  return (
    <Form
      form={form}
      layout={"vertical"}
      onFinish={saveRecipe}
      onFinishFailed={() =>
        notify.error({
          title: "Recipe creation failed",
          description: "Please fill out all input fields",
        })
      }
      style={{ flex: 1 }}
    >
      <Flex vertical gap={tokens.gap.small}>
        <Form.Item
          name={RECIPE_CREATION_FORM_FIELDS.title}
          label={"Recipe Name"}
          rules={[
            { required: true, whitespace: true, message: "Enter a name" },
          ]}
          style={{ marginBottom: 0 }}
        >
          <Input size={"large"} placeholder={"Recipe Name"} />
        </Form.Item>

        <Form.Item
          name={RECIPE_CREATION_FORM_FIELDS.images}
          valuePropName={"fileList"}
          getValueFromEvent={(e) => (Array.isArray(e) ? e : e?.fileList)}
          style={{ marginBottom: 0 }}
        >
          <Upload
            listType={"picture"}
            accept={ALLOWED_IMAGE_TYPES.join(",")}
            maxCount={MAX_IMAGE_AMOUNT}
            beforeUpload={beforeImageUpload}
            customRequest={customImageUploadRequest}
            styles={{
              root: {
                display: "flex",
                flexDirection: "column-reverse",
                gap: images.length > 0 ? tokens.gap.small : 0,
              },
              trigger: { width: "100%" },
            }}
          >
            {images.length < MAX_IMAGE_AMOUNT && (
              <Button
                block
                type={"dashed"}
                size={"large"}
                icon={<UploadOutlined />}
                loading={isUploading}
              >
                Add image
              </Button>
            )}
          </Upload>
        </Form.Item>
      </Flex>

      <SectionHeader title={"Ingredients"} />
      <IngredientSelectionList form={form} ingredientClient={ingredient} />

      <SectionHeader title={"Steps"} />
      <StepsList />

      <Button
        type="primary"
        htmlType="submit"
        size="large"
        block
        disabled={isUploading}
        style={{ marginTop: tokens.margin.large }}
      >
        Save recipe
      </Button>
    </Form>
  );
};
