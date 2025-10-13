import { ApiError } from '@/apis/apiClient';
import {
  ImageExtension,
  postPresignedUrl,
  PostPresignedUrlParams,
} from '@/apis/s3.api';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

export default function useUploadImage() {
  const [file, setFile] = useState<File | null>(null);
  const [imgUrl, setImgUrl] = useState<string | null>(null);
  const [presignedUrl, setPresignedUrl] = useState<string | null>(null);
  const [contentType, setContentType] = useState<string | null>(null);
  const { handleApiError } = useApiErrorHandler();

  const onChangeFile: React.ChangeEventHandler<HTMLInputElement> = async (
    e
  ) => {
    const file = e.target.files?.[0] ?? null;
    const extension = file?.name.split('.').pop();
    if (!file || !extension) return;

    setFile(file);

    fetchPresignedUrl({
      extension: extension as ImageExtension,
      objectDir: 'feedback_media',
    });
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
    onError: (error) => {
      handleApiError(error as ApiError);
    },
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
