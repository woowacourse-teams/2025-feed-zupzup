import { resizeImage } from '@/domains/utils/resizeImage';
import { useCallback } from 'react';

export default function useImageCompression() {
  const compressImage = useCallback(async (file: File) => {
    const contentType = file.name.split('.').pop();
    return await resizeImage({ file, contentType });
  }, []);

  return { compressImage };
}
