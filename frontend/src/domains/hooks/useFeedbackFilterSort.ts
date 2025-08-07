import { useState } from 'react';
import { FeedbackType, FeedbackFilter, SortType } from '@/types/feedback.types';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';

export default function useFeedbackFilterSort(feedbacks: FeedbackType[]) {
  const [selectedFilter, setSelectedFilter] = useState<FeedbackFilter | null>(
    null
  );
  const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');
  const { getIsMyFeedback } = useMyFeedbacks();

  const handleFilterChange = (newFilter: FeedbackFilter | null) => {
    setSelectedFilter(newFilter);
  };

  const handleSortChange = (newSort: SortType) => {
    setSelectedSort(newSort);
  };

  const filteredAndSortedFeedbacks = feedbacks.filter((feedback) => {
    if (selectedFilter === '나의글') {
      return getIsMyFeedback(feedback.feedbackId);
    }

    return true;
  });

  return {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    filteredAndSortedFeedbacks,
  };
}
