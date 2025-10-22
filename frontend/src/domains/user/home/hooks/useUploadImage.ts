import {
  ImageExtension,
  postPresignedUrl,
  PostPresignedUrlParams,
} from '@/apis/s3.api';
import { useMutation } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

export default function useUploadImage() {
  const [file, setFile] = useState<File | null>(null);
  const [imgUrl, setImgUrl] = useState<string | null>(null);
  const [presignedUrl, setPresignedUrl] = useState<string | null>(null);
  const [contentType, setContentType] = useState<string | null>(null);

  const onChangeFile: React.ChangeEventHandler<HTMLInputElement> = async (
    e
  ) => {
    try {
      const file = e.target.files?.[0] ?? null;
      const extension = file?.name.split('.').pop();
      if (!file || !extension) return;

      setFile(file);

      await fetchPresignedUrl({
        extension: extension as ImageExtension,
        objectDir: 'feedback_media',
      });
    } catch (e) {
      console.error(e);
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
