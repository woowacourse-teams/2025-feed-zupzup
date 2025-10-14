import { useState, useCallback } from 'react';
import { FeedbackFilterType, SortType } from '@/types/feedback.types';

import { useMyFeedbackData } from '../user/userDashboard/hooks/useMyFeedbackData';

export default function useFeedbackFilterSort() {
  const [selectedFilter, setSelectedFilter] = useState<FeedbackFilterType | ''>(
    ''
  );
  const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');

  const { myFeedbacks } = useMyFeedbackData();

  const handleFilterChange = useCallback(
    (newFilter: FeedbackFilterType | '') => {
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
