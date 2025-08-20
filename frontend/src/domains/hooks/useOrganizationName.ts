import { getOrganizationName } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useQuery } from '@tanstack/react-query';

interface UseOrganizationNameProps {
  organizationId: string;
}

export default function useOrganizationName({
  organizationId,
}: UseOrganizationNameProps) {
  const { showErrorModal } = useErrorModalContext();

  const { data, error } = useQuery({
    queryKey: QUERY_KEYS.organizationData(organizationId),
    queryFn: async () => {
      const response = await getOrganizationName({ organizationId });
      return response.data;
    },
  });

  if (error) {
    showErrorModal('서버에서 에러가 발생했습니다. 다시 시도해 주세요.', '에러');
  }

  return {
    groupName: data?.organizationName || '피드줍줍',
    totalCheeringCount: data?.totalCheeringCount || 0,
    categories: data?.categories || [],
  };
}
