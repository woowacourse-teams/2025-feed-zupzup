import { FeedbackFilterType } from '@/types/feedback.types';
import { useCallback, useState } from 'react';

export default function useFeedbackFilter() {
  const [filter, setFilter] = useState<FeedbackFilterType>('ALL');

  const handlePanelClick = useCallback(
    (category: FeedbackFilterType) => {
      if (filter === category) {
        setFilter('ALL');
        return;
      }

      setFilter(category);
    },
    [filter]
  );

  return { filter, handlePanelClick };
}
