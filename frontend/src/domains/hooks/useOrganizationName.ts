import { ApiError } from '@/apis/apiClient';
import { getOrganizationName } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';

interface UseOrganizationNameProps {
  organizationId: string;
}

export default function useOrganizationName({
  organizationId,
}: UseOrganizationNameProps) {
  const { showErrorModal } = useErrorModalContext();
  const { handleApiError } = useApiErrorHandler();

  const { data, error } = useQuery({
    queryKey: [...QUERY_KEYS.organizationData, organizationId],
    queryFn: async () => {
      const response = await getOrganizationName({ organizationId });
      return response.data;
    },
  });

  if (error) {
    showErrorModal(
      '조직 정보를 불러오는데 실패했습니다. 다시 시도해 주세요.',
      '에러'
    );
    handleApiError(error as ApiError);
  }

  return {
    groupName: data?.organizationName || '피드줍줍',
    totalCheeringCount: data?.totalCheeringCount || 0,
    categories: data?.categories || [],
  };
}
