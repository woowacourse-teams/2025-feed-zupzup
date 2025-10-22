import { getOrganizationName } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

interface UseOrganizationNameProps {
  organizationId: string;
}

export default function useOrganizationName({
  organizationId,
}: UseOrganizationNameProps) {
  const { data, isLoading } = useQuery({
    queryKey: [...QUERY_KEYS.organizationData, organizationId],
    queryFn: async () => {
      const response = await getOrganizationName({ organizationId });
      return response.data;
    },
  });

  return {
    groupName: data?.organizationName || '피드줍줍',
    totalCheeringCount: data?.totalCheeringCount || 0,
    categories: data?.categories || [],
    isLoading,
  };
}
