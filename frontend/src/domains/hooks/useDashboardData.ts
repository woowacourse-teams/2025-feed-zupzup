import { useMemo } from 'react';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';

interface UseDashboardDataOptions {
  isAdmin?: boolean;
}

export default function useDashboardData({
  isAdmin = false,
}: UseDashboardDataOptions = {}) {
  const {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    getFilteredFeedbacks,
  } = useFeedbackFilterSort();

  const apiUrl = useMemo(() => {
    const baseUrl = isAdmin
      ? '/admin/organizations/1/feedbacks'
      : '/organizations/1/feedbacks';
    const params = new URLSearchParams();

    params.append('orderBy', selectedSort);

    if (selectedFilter === 'COMPLETED') {
      params.append('status', 'CONFIRMED');
    } else if (selectedFilter === 'PENDING') {
      params.append('status', 'WAITING');
    }

    return `${baseUrl}?${params.toString()}`;
  }, [selectedFilter, selectedSort, isAdmin]);

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    FeedbackType,
    'feedbacks',
    FeedbackResponse<FeedbackType>
  >({
    url: apiUrl,
    key: 'feedbacks',
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  const filteredFeedbacks = getFilteredFeedbacks(feedbacks);

  return {
    feedbacks: filteredFeedbacks,
    loading,
    hasNext,
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
  };
}
