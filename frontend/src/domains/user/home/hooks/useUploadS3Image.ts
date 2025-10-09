import { ApiError } from '@/apis/apiClient';
import { uploadImageToS3 } from '@/apis/s3.api';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation } from '@tanstack/react-query';

export function useUploadS3Image() {
  const { handleApiError } = useApiErrorHandler();

  const { mutate: uploadS3PreSignUrl } = useMutation({
    mutationFn: ({
      presignedUrl,
      file,
      contentType,
    }: {
      presignedUrl: string;
      file: File;
      contentType: string;
    }) =>
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
