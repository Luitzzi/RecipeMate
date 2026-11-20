import { Upload, type UploadProps } from "antd";

import { useApi } from "#/api/apiContext.ts";
import { putFile } from "#/api/putFile.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import { validateImage } from "#/common/imageUploadUtils.ts";

/**
 * Returns `beforeUpload` and `customRequest` handlers for an antd `Upload`.
 * Files are validated, then uploaded to a presigned url.
 * On success the file's `response` is `{ key }` (the image key for the backend).
 */
export const useImageUpload = () => {
  const api = useApi();
  const notify = useNotification();

  const [uploadImage] = useMutation(async (file: File) => {
    const { key, url } = await api.image.getUploadUrl({
      contentType: file.type,
    });
    await putFile(url, file);
    return key;
  });

  const beforeUpload: UploadProps["beforeUpload"] = (file) => {
    const validationError = validateImage(file);
    if (!validationError) return true;

    notify.error({ title: validationError });
    return Upload.LIST_IGNORE;
  };

  const customRequest: UploadProps["customRequest"] = async ({
    file,
    onSuccess,
    onError,
  }) => {
    const key = await uploadImage(file as File).catch((err: Error) =>
      notify.error({
        title: "Upload fehlgeschlagen",
        description: err.message,
      }),
    );
    if (key) onSuccess?.({ key });
    else onError?.(new Error("Upload failed"));
  };

  return { beforeUpload, customRequest };
};
