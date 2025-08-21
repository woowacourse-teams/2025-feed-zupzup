import { getFeedbackStatistics } from '@/apis/adminFeedback.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';

export default function useAdminStatistics() {
  const { data, isLoading, error } = useQuery({
    queryKey: QUERY_KEYS.adminFeedbackStatistics,
    queryFn: getFeedbackStatistics,
  });

  const { handleApiError } = useApiErrorHandler();
  const { showErrorModal } = useErrorModalContext();

  if (error) {
    handleApiError(error as ApiError);
    showErrorModal(
      '관리자 통계 정보를 불러오는 데 실패했습니다. 다시 시도해 주세요',
      '관리자 통계 에러'
    );
    return { statistics: null, isLoading: false };
  }

  return {
    statistics: data?.data,
    isStatisticsLoading: isLoading,
  };
}
