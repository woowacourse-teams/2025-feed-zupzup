import { ApiError } from '@/apis/apiClient';
import { putOrganizations } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useErrorModal } from '@/hooks/useErrorModal';
import { PutOrganizationsBody } from '@/types/organization.types';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export default function useEditRoom({
  organizationName,
  categories,
}: PutOrganizationsBody) {
  const { showErrorModal } = useErrorModal();
  const { organizationId } = useOrganizationId();
  const { handleApiError } = useApiErrorHandler();
  const queryClient = useQueryClient();

  const mutate = useMutation({
    mutationFn: () =>
      putOrganizations({ organizationId, organizationName, categories }),
    onError: (error) => {
      showErrorModal(
        '방 수정을 실패했습니다. 다시 시도해 주세요',
        '방 수정 실패'
      );
      handleApiError(error as ApiError);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.infiniteFeedbacks });
      queryClient.invalidateQueries({
        queryKey: QUERY_KEYS.organizationData,
      });
    },
  });

  return {
    editRoom: mutate.mutateAsync,
    isLoading: mutate.isPending,
  };
}
