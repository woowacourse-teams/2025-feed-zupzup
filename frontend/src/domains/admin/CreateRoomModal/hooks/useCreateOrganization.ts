import { postAdminOrganization } from '@/apis/adminOrganization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UseCreateOrganizationParams {
  onClose: () => void;
  name: string;
}

export default function useCreateOrganization({
  onClose,
  name,
}: UseCreateOrganizationParams) {
  const queryClient = useQueryClient();

  const { mutate: createRoom, isPending } = useMutation({
    mutationFn: postAdminOrganization,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: QUERY_KEYS.adminOrganizations(name),
      });
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
