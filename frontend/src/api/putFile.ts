/**
 * Uploads a file to a presigned url.
 * @param url Presigned upload url
 * @param file File to upload, its type has to match the signed content type
 */
export const putFile = async (url: string, file: File): Promise<void> => {
  const response = await fetch(url, {
    method: "PUT",
    headers: { "Content-Type": file.type },
    body: file,
  });

  if (!response.ok) {
    throw new Error(`PUT upload failed with status ${response.status}`);
  }
};
