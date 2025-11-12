import imageCompression from 'browser-image-compression';

export async function resizeImage({
  file,
  contentType,
}: {
  file: File;
  contentType: string | undefined;
}) {
  // return { newFile: file, newContentType: contentType ?? 'png' };
  const outputType = 'webp';

  const options = {
    maxSizeMB: 1,
    maxWidthOrHeight: 700,
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
