import { PlusOutlined } from "@ant-design/icons";
import { Form, Input, Modal, Spin, Upload, type UploadFile } from "antd";
import { useEffect } from "react";

import { useImageUpload } from "#/common/hooks/useImageUpload.ts";
import { useImageUrls } from "#/common/hooks/useImageUrls.ts";
import { ALLOWED_IMAGE_TYPES } from "#/common/imageUploadUtils.ts";
import type { FolderUiDTO } from "#/generated/interface-api.ts";

type FolderFormValues = {
  name: string;
  image: UploadFile<{ key: string }>[];
};

export type FolderModalProps = {
  open: boolean;
  editMode: boolean;
  saving: boolean;
  initialFolder?: FolderUiDTO;
  onSubmit: (values: { name: string; imageKey?: string }) => void;
  onCancel: () => void;
};

export const FolderModal = ({
  open,
  editMode,
  saving,
  initialFolder,
  onSubmit,
  onCancel,
}: FolderModalProps) => {
  const [form] = Form.useForm<FolderFormValues>();
  const { beforeUpload, customRequest } = useImageUpload();
  const getImageUrl = useImageUrls();

  const image = Form.useWatch("image", form) ?? [];
  const isUploading = image.some((file) => file.status === "uploading");

  // --- Existing image ---
  const initialImageKey = initialFolder?.imageKey;
  const initialImageUrl = getImageUrl(initialImageKey);
  const loadingImage = !!initialImageKey && !initialImageUrl;

  useEffect(() => {
    if (!open || !initialImageUrl) return;
    const files: FolderFormValues["image"] = form.getFieldValue("image") ?? [];
    form.setFieldValue(
      "image",
      files.map((file) =>
        file.uid === initialImageKey && !file.url
          ? { ...file, url: initialImageUrl }
          : file,
      ),
    );
  }, [open, form, initialImageKey, initialImageUrl]);

  // --- Render ---
  return (
    <Modal
      title={editMode ? "Ordner bearbeiten" : "Neuer Ordner"}
      open={open}
      okText={editMode ? "Speichern" : "Erstellen"}
      cancelText={"Abbrechen"}
      confirmLoading={saving}
      okButtonProps={{ disabled: isUploading }}
      onOk={() => form.submit()}
      onCancel={onCancel}
      destroyOnHidden
    >
      <Form
        form={form}
        layout={"vertical"}
        preserve={false}
        onFinish={({ name, image }) =>
          onSubmit({ name: name.trim(), imageKey: image[0]?.response?.key })
        }
        initialValues={
          {
            name: initialFolder?.name ?? "",
            image: initialImageKey
              ? [
                  {
                    uid: initialImageKey,
                    name: "Bild",
                    status: "done",
                    response: { key: initialImageKey },
                  },
                ]
              : [],
          } satisfies FolderFormValues
        }
      >
        <Form.Item
          name={"name"}
          label={"Name"}
          rules={[
            {
              required: true,
              whitespace: true,
              message: "Bitte einen Namen eingeben",
            },
          ]}
        >
          <Input autoFocus placeholder={"z. B. Lieblingsrezepte"} />
        </Form.Item>

        <Form.Item label={"Bild"}>
          <Spin spinning={loadingImage}>
            <Form.Item
              name={"image"}
              noStyle
              valuePropName={"fileList"}
              getValueFromEvent={(e) => (Array.isArray(e) ? e : e?.fileList)}
            >
              <Upload
                listType={"picture-card"}
                accept={ALLOWED_IMAGE_TYPES.join(",")}
                maxCount={1}
                disabled={loadingImage}
                beforeUpload={beforeUpload}
                customRequest={customRequest}
              >
                {image.length === 0 && <PlusOutlined />}
              </Upload>
            </Form.Item>
          </Spin>
        </Form.Item>
      </Form>
    </Modal>
  );
};
