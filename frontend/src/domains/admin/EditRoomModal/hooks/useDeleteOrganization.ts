import { ApiError } from '@/apis/apiClient';
import { deleteAdminOrganization } from '@/apis/adminOrganization.api';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useMutation } from '@tanstack/react-query';
import useNavigation from '@/domains/hooks/useNavigation';

export default function useDeleteOrganization() {
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
      goPath('/admin/home');
    },
  });

  return {
    deleteOrganization: mutate.mutateAsync,
    isLoading: mutate.isPending,
  };
}
