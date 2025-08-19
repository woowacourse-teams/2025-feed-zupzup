import { putOrganizations } from '@/apis/organization.api';
import { CategoryListType } from '@/constants/categoryList';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UseEditRoomParams {
  organizationName: string;
  categories: CategoryListType[];
}

export default function useEditRoom({
  organizationName,
  categories,
}: UseEditRoomParams) {
  const { showErrorModal } = useErrorModalContext();
  const { organizationId } = useOrganizationId();
  const queryClient = useQueryClient();

  const mutate = useMutation({
    mutationFn: () =>
      putOrganizations({ organizationId, organizationName, categories }),
    onError: () =>
      showErrorModal(
        '방 수정을 실패했습니다. 다시 시도해 주세요',
        '방 수정 실패'
      ),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['infinity', 'feedbacks'] });
      queryClient.invalidateQueries({
        queryKey: ['organizationData'],
      });
    },
  });

  return {
    editRoom: mutate.mutateAsync,
    isLoading: mutate.isPending,
  };
}
