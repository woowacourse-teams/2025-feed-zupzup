import {
  ImageExtension,
  postPresignedUrl,
  PostPresignedUrlParams,
} from '@/apis/s3.api';
import { useToast } from '@/contexts/useToast';
import { resizeImage } from '@/domains/utils/resizeImage';
import { useMutation } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

export default function useUploadImage() {
  const [file, setFile] = useState<File | null>(null);
  const [imgUrl, setImgUrl] = useState<string | null>(null);
  const [presignedUrl, setPresignedUrl] = useState<string | null>(null);
  const [contentType, setContentType] = useState<string | null>(null);
  const { showToast } = useToast();

  const onChangeFile: React.ChangeEventHandler<HTMLInputElement> = async (
    e
  ) => {
    try {
      if (!e.target.files || e.target.files.length === 0) return;

      const file = e.target.files?.[0] ?? null;
      const contentType = file?.name.split('.').pop();

      const { newFile, newContentType } = await resizeImage({
        file,
        contentType,
      });

      if (!newFile || !newContentType) return;

      setFile(newFile);

      await fetchPresignedUrl({
        extension: newContentType as ImageExtension,
        objectDir: 'feedback_media',
      });
    } catch (e) {
      if (e instanceof Error)
        showToast(e.message || '이미지 업로드에 실패했습니다.');
      return;
    }
  };

  const handleCancelFile = () => {
    setFile(null);
    setImgUrl(null);
  };

  useEffect(() => {
    if (!file) {
      if (imgUrl) URL.revokeObjectURL(imgUrl);
      setImgUrl(null);
      return;
    }
    const url = URL.createObjectURL(file);
    setImgUrl(url);
    return () => URL.revokeObjectURL(url);
  }, [file]);

  const { mutateAsync: fetchPresignedUrl } = useMutation({
    mutationFn: ({ extension, objectDir }: PostPresignedUrlParams) =>
      postPresignedUrl({ extension, objectDir }),
    onSuccess: (response) => {
      setPresignedUrl(response.data.presignedUrl);
      setContentType(response.data.contentType);
    },
  });

  return {
    file,
    imgUrl,
    onChangeFile,
    fetchPresignedUrl,
    presignedUrl,
    contentType,
    handleCancelFile,
  };
}
