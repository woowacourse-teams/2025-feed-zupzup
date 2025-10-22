import { ApiError } from '@/apis/apiClient';
import { deleteAdminOrganization } from '@/apis/adminOrganization.api';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import useNavigation from '@/domains/hooks/useNavigation';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';

export default function useDeleteOrganization() {
  const adminName =
    getLocalStorage<AdminAuthData>('auth')?.adminName || '관리자';
  const queryClient = useQueryClient();
  const { organizationId } = useOrganizationId();
  const { handleApiError } = useApiErrorHandler();
  const { goPath } = useNavigation();

  const mutate = useMutation({
    mutationFn: () =>
      deleteAdminOrganization({ organizationUuid: organizationId }),
    onError: (error) => {
      handleApiError(error as ApiError);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: QUERY_KEYS.adminOrganizations(adminName),
      });
      goPath('/admin/home');
    },
  });

  return {
    deleteOrganization: mutate.mutateAsync,
    isDeleting: mutate.isPending,
  };
}
