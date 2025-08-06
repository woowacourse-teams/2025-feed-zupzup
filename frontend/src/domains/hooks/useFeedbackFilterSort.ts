import { useState, useMemo } from 'react';
import { FeedbackType } from '@/types/feedback.types';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';

export default function useFeedbackFilterSort(feedbacks: FeedbackType[]) {
  const [selectedFilter, setSelectedFilter] = useState<string | null>(null);
  const [selectedSort, setSelectedSort] = useState('latest');
  const { getIsMyFeedback } = useMyFeedbacks();

  const handleFilterChange = (newFilter: string | null) => {
    setSelectedFilter(newFilter);
  };

  const handleSortChange = (newSort: string) => {
    setSelectedSort(newSort);
  };

  const filteredAndSortedFeedbacks = useMemo(() => {
    let filtered = [...feedbacks];

    if (selectedFilter) {
      switch (selectedFilter) {
        case 'pending':
          filtered = filtered.filter(
            (feedback) => feedback.status === 'WAITING'
          );
          break;
        case 'completed':
          filtered = filtered.filter(
            (feedback) => feedback.status === 'CONFIRMED'
          );
          break;
        case 'mine':
          filtered = filtered.filter((feedback) =>
            getIsMyFeedback(feedback.feedbackId)
          );
          break;
      }
    }

    switch (selectedSort) {
      case 'oldest':
        filtered.sort(
          (a, b) =>
            new Date(a.postedAt).getTime() - new Date(b.postedAt).getTime()
        );
        break;
      case 'likes':
        filtered.sort((a, b) => b.likeCount - a.likeCount);
        break;
      case 'latest':
      default:
        filtered.sort(
          (a, b) =>
            new Date(b.postedAt).getTime() - new Date(a.postedAt).getTime()
        );
        break;
    }

    return filtered;
  }, [feedbacks, selectedFilter, selectedSort, getIsMyFeedback]);

  return {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    filteredAndSortedFeedbacks,
  };
}
