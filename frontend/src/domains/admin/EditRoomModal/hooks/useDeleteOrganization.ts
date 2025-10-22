import { deleteAdminOrganization } from '@/apis/adminOrganization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import useNavigation from '@/domains/hooks/useNavigation';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export default function useDeleteOrganization() {
  const adminName =
    getLocalStorage<AdminAuthData>('auth')?.adminName || '관리자';
  const queryClient = useQueryClient();
  const { organizationId } = useOrganizationId();
  const { goPath } = useNavigation();

  const mutate = useMutation({
    mutationFn: () =>
      deleteAdminOrganization({ organizationUuid: organizationId }),
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
