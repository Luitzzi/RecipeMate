const MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
export const ALLOWED_IMAGE_TYPES = ["image/jpeg", "image/png", "image/webp"];

export const validateImage = (file: File): string | undefined => {
  if (!ALLOWED_IMAGE_TYPES.includes(file.type))
    return "Nur JPG, PNG oder WebP erlaubt";
  if (file.size > MAX_IMAGE_SIZE_BYTES) return "Datei ist größer als 5 MB";
  return undefined;
};
