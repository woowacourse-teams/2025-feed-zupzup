import { ApiError } from '@/apis/apiClient';
import { uploadImageToS3 } from '@/apis/s3.api';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation } from '@tanstack/react-query';

interface UploadS3ImageParams {
  presignedUrl: string;
  file: File;
  contentType: string;
}

export function useUploadS3Image() {
  const { handleApiError } = useApiErrorHandler();

  const { mutateAsync: uploadS3PreSignUrl } = useMutation({
    mutationFn: ({ presignedUrl, file, contentType }: UploadS3ImageParams) =>
      uploadImageToS3({
        presignedUrl,
        file,
        contentType,
      }),
    onError: (error) => {
      handleApiError(error as ApiError);
    },
  });

  return { uploadS3PreSignUrl };
}
