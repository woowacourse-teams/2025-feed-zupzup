import { validateImageSignature } from '@/domains/user/home/utils/validateImageSignature';
import { resizeImage } from '@/domains/utils/resizeImage';
import { useCallback } from 'react';

const ALLOWED_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'image/tiff',
  'image/bmp',
  'image/svg+xml',
  'image/x-icon',
  'image/heic',
  'image/heif',
  'image/x-raw',
  'image/vnd.adobe.photoshop',
];

export default function useImageCompression() {
  const compressImage = useCallback(async (file: File) => {
    if (!ALLOWED_TYPES.includes(file.type)) {
      throw new Error('지원하지 않는 이미지 형식입니다.');
    }

    if (file.size > 10 * 1024 * 1024) {
      throw new Error('파일 크기는 10MB 이하여야 합니다.');
    }

    const isValidImage = await validateImageSignature(file);
    if (!isValidImage) {
      throw new Error('유효하지 않은 이미지 파일입니다.');
    }

    const contentType = file.type.split('/')[1] || 'png';
    return await resizeImage({ file, contentType });
  }, []);

  return { compressImage };
}
