import {
  Button,
  Form,
  Input,
  Upload,
  type UploadFile,
  type UploadProps,
} from "antd";
import { useEffect } from "react";
import {
  ALLOWED_IMAGE_TYPES,
  RECIPE_CREATION_FORM_FIELDS,
  type RecipeCreationFormValues,
  validateImage,
} from "#/page/recipe_creation/utils/recipeCreationFormUtils.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import { useApi } from "#/api/apiContext.ts";
import { UploadOutlined } from "@ant-design/icons";
import { putFile } from "#/api/putFile.ts";
import { IngredientSelect } from "#/page/recipe_creation/components/IngredientSelect.tsx";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { StepCreationCard } from "#/page/recipe_creation/components/StepCreationCard.tsx";

export const RecipeCreationForm = () => {
  const [form] = Form.useForm<RecipeCreationFormValues>();
  const api = useApi();
  const notify = useNotification();

  const images: UploadFile[] =
    Form.useWatch(RECIPE_CREATION_FORM_FIELDS.images, form) ?? [];
  // Derived from the file list, because the mutation state is shared by parallel uploads
  const isUploading = images.some((image) => image.status === "uploading");

  const [uploadImage, , , uploadError] = useMutation(async (file: File) => {
    const { key, url } = await api.image.getUploadUrl({
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [uploadError]);

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

  return (
    <Form form={form}>
      <Form.Item
        name={RECIPE_CREATION_FORM_FIELDS.recipeName}
        label={"Recipe Name"}
        rules={[{ required: true, whitespace: true, message: "Enter a name" }]}
      >
        <Input size={"large"} placeholder={"Recipe Name"} />
      </Form.Item>

      <Form.Item
        name={RECIPE_CREATION_FORM_FIELDS.images}
        label={"Images"}
        valuePropName={"fileList"}
        getValueFromEvent={(e) => (Array.isArray(e) ? e : e?.fileList)}
      >
        <Upload
          listType={"picture"}
          accept={ALLOWED_IMAGE_TYPES.join(",")}
          maxCount={5}
          beforeUpload={beforeImageUpload}
          customRequest={customImageUploadRequest}
        >
          <Button icon={<UploadOutlined />} loading={isUploading}>
            Bild auswählen
          </Button>
        </Upload>
      </Form.Item>

      <SectionHeader title={"Ingredients"} />
      <IngredientSelect form={form} ingredientClient={api.ingredient} />

      <SectionHeader title={"Steps"} />
      <StepCreationCard />
    </Form>
  );
};
