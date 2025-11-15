import imageCompression from 'browser-image-compression';

const MAX_WIDTH_OR_HEIGHT = 700;

export async function resizeImage({
  file,
  contentType,
}: {
  file: File;
  contentType: string | undefined;
}) {
  const outputType = 'webp';

  const options = {
    maxSizeMB: 1,
    maxWidthOrHeight: MAX_WIDTH_OR_HEIGHT,
    fileType: outputType,
    initialQuality: 0.8,
    useWebWorker: true,
  } as const;

  try {
    const compressed: File = await imageCompression(file, options);
    return { newFile: compressed, newContentType: outputType };
  } catch (error) {
    console.error('Image compression error:', error);
    return { newFile: file, newContentType: contentType ?? 'png' };
  }
}
