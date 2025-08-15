import { useState, useCallback } from 'react';
import { FeedbackFilterType, SortType } from '@/types/feedback.types';

import { useMyFeedbackData } from '../user/userDashboard/hooks/useMyFeedbackData';

export default function useFeedbackFilterSort() {
  const [selectedFilter, setSelectedFilter] =
    useState<FeedbackFilterType | null>(null);
  const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');

  const { myFeedbacks } = useMyFeedbackData(selectedSort);

  const handleFilterChange = useCallback(
    (newFilter: FeedbackFilterType | null) => {
      setSelectedFilter(newFilter);
    },
    []
  );

  const handleSortChange = useCallback((newSort: SortType) => {
    setSelectedSort(newSort);
  }, []);

  return {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    myFeedbacks,
  };
}
