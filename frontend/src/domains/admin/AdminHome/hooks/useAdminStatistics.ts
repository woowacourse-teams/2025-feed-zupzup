import { getFeedbackStatistics } from '@/apis/adminFeedback.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
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
  const { data, isLoading, error } = useQuery({
    queryKey: QUERY_KEYS.adminFeedbackStatistics(adminName),
    queryFn: getFeedbackStatistics,
    retry: false,
  });

  const { handleApiError } = useApiErrorHandler();
  const { showErrorModal } = useErrorModalContext();

  if (error) {
    showErrorModal(
      '관리자 통계 정보를 불러오는 데 실패했습니다. 다시 시도해 주세요',
      '관리자 통계 에러'
    );
    handleApiError(error as ApiError);

    return {
      statistics: DEFAULT_STATISTICS,
      isLoading: false,
    };
  }

  return {
    statistics: data ? data.data : DEFAULT_STATISTICS,
    isLoading,
    isStatisticsLoading: isLoading,
  };
}
