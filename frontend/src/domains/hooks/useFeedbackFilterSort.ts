import { useState } from 'react';
import { FeedbackType } from '@/types/feedback.types';

export default function useFeedbackFilterSort(feedbacks: FeedbackType[]) {
  const [selectedFilter, setSelectedFilter] = useState<string | null>(null);
  const [selectedSort, setSelectedSort] = useState('latest');

  const handleFilterChange = (newFilter: string | null) => {
    setSelectedFilter(newFilter);
  };

  const handleSortChange = (newSort: string) => {
    setSelectedSort(newSort);
  };

  const filteredAndSortedFeedbacks = feedbacks;

  return {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    filteredAndSortedFeedbacks,
  };
}
