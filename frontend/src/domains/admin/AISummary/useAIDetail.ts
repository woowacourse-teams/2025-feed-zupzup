import { getAISummaryDetail } from '@/apis/admin.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

interface UseGetAIDetailProps {
  organizationId: string;
  clusterId: number;
}

export default function useAIDetail({
  organizationId,
  clusterId,
}: UseGetAIDetailProps) {
  const { data, isLoading } = useQuery({
    queryKey: QUERY_KEYS.aiSummaryDetail(organizationId, clusterId),
    queryFn: () => getAISummaryDetail({ organizationId, clusterId }),
  });

  return { data: data?.data, isLoading };
}
