import { getAISummary } from '@/apis/admin.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

interface UseAISummaryProps {
  organizationId: string;
}

export default function useAISummary({ organizationId }: UseAISummaryProps) {
  const { data, isLoading } = useQuery({
    queryKey: QUERY_KEYS.aiSummary(organizationId),
    queryFn: () => getAISummary({ organizationId }),
  });

  return { data: data?.data, isLoading };
}
