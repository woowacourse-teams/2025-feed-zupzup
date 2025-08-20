import { postAdminOrganization } from '@/apis/adminOrganization.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UseCreateOrganizationParams {
  onClose: () => void;
}

export default function useCreateOrganization({
  onClose,
}: UseCreateOrganizationParams) {
  const { handleApiError } = useApiErrorHandler();
  const queryClient = useQueryClient();

  const { mutate: createRoom, isPending } = useMutation({
    mutationFn: postAdminOrganization,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: QUERY_KEYS.adminOrganizations(),
      });
      onClose();
    },
    onError: (error: ApiError) => {
      handleApiError(error);
    },
    onSettled: () => {
      onClose();
    },
  });

  return {
    createRoom,
    isPending,
  };
}
