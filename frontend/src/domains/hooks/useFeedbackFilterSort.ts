import { useState } from 'react';
import {
  FeedbackType,
  FeedbackFilterType,
  SortType,
} from '@/types/feedback.types';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';

export default function useFeedbackFilterSort(feedbacks: FeedbackType[]) {
  const [selectedFilter, setSelectedFilter] =
    useState<FeedbackFilterType | null>(null);
  const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');
  const { getIsMyFeedback } = useMyFeedbacks();

  const handleFilterChange = (newFilter: FeedbackFilterType | null) => {
    setSelectedFilter(newFilter);
  };

  const handleSortChange = (newSort: SortType) => {
    setSelectedSort(newSort);
  };

  const filteredAndSortedFeedbacks = feedbacks.filter((feedback) => {
    if (selectedFilter === 'MINE') {
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
