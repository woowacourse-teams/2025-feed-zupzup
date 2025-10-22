import { getFeedbackStatistics } from '@/apis/adminFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

const DEFAULT_STATISTICS = {
  confirmedCount: 0,
  totalCount: 0,
  reflectionRate: 0,
};

interface UseAdminStatisticsParams {
  adminName: string;
}

export default function useAdminStatistics({
  adminName,
}: UseAdminStatisticsParams) {
  const { data, isLoading } = useQuery({
    queryKey: QUERY_KEYS.adminFeedbackStatistics(adminName),
    queryFn: getFeedbackStatistics,
    retry: false,
  });

  return {
    statistics: data ? data.data : DEFAULT_STATISTICS,
    isLoading,
    isStatisticsLoading: isLoading,
  };
}
