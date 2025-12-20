import { putOrganizations } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { PutOrganizationsBody } from '@/types/organization.types';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export default function useEditRoom({
  organizationName,
  categories,
}: PutOrganizationsBody) {
  const { organizationId } = useOrganizationId();
  const queryClient = useQueryClient();

  const mutate = useMutation({
    mutationFn: () =>
      putOrganizations({ organizationId, organizationName, categories }),
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
