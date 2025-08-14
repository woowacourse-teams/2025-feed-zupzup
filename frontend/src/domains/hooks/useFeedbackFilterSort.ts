import { useState, useCallback } from 'react';
import {
  FeedbackFilterType,
  FeedbackType,
  SortType,
} from '@/types/feedback.types';

import { useMyFeedbackData } from '../user/userDashboard/hooks/useMyFeedbackData';

export default function useFeedbackFilterSort() {
  const [selectedFilter, setSelectedFilter] =
    useState<FeedbackFilterType | null>(null);
  const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');
  const { myFeedbacks } = useMyFeedbackData();

  const handleFilterChange = useCallback(
    (newFilter: FeedbackFilterType | null) => {
      setSelectedFilter(newFilter);
    },
    []
  );

  const handleSortChange = useCallback((newSort: SortType) => {
    setSelectedSort(newSort);
  }, []);

  const getFilteredFeedbacks = useCallback(
    (feedbacks: FeedbackType[]) => {
      const uniqueFeedbacks = feedbacks.filter(
        (feedback, index, arr) =>
          arr.findIndex((f) => f.feedbackId === feedback.feedbackId) === index
      );

      if (selectedFilter === 'MINE') {
        return myFeedbacks;
      }

      return uniqueFeedbacks;
    },
    [selectedFilter, myFeedbacks]
  );

  return {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    getFilteredFeedbacks,
    myFeedbacks,
  };
}
