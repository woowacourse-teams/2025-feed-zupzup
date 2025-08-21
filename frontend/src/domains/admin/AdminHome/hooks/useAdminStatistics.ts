import { getFeedbackStatistics } from '@/apis/adminFeedback.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';

export default function useAdminStatistics() {
  const { data, isLoading, error } = useQuery({
    queryKey: QUERY_KEYS.adminFeedbackStatistics,
    queryFn: getFeedbackStatistics,
  });

  const { handleApiError } = useApiErrorHandler();

  if (error) {
    handleApiError(error as ApiError);
    return { statistics: null, isLoading: false };
  }

  return {
    statistics: data?.data,
    isStatisticsLoading: isLoading,
  };
}
