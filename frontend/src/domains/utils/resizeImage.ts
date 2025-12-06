import imageCompression from 'browser-image-compression';

const MAX_WIDTH_OR_HEIGHT = 700;

export async function resizeImage({
  file,
  contentType,
}: {
  file: File;
  contentType: string;
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
    console.error('이미지 압축 실패. 원본 파일을 반환합니다.', error);
    return { newFile: file, newContentType: contentType };
  }
}
