import { useEffect } from 'react';
import { FeedbackFilterType, SortType } from '@/types/feedback.types';

export default function useSortResetOnMineFilter(
  selectedFilter: FeedbackFilterType | '',
  selectedSort: SortType,
  onSortChange: (sort: SortType) => void
) {
  useEffect(() => {
    if (selectedFilter === 'MINE' && selectedSort !== 'LATEST') {
      onSortChange('LATEST');
    }
  }, [selectedFilter, selectedSort, onSortChange]);
}
