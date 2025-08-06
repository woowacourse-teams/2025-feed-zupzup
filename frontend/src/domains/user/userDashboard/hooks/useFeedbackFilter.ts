import { FeedbackFilter } from '@/types/feedback.types';
import { useCallback, useState } from 'react';

export default function useFeedbackFilter() {
  const [filter, setFilter] = useState<FeedbackFilter>('전체');

  const handlePanelClick = useCallback(
    (category: FeedbackFilter) => {
      console.log(filter, category);
      if (filter === category) {
        setFilter('전체');
        return;
      }

      setFilter(category);
    },
    [filter]
  );

  return { filter, handlePanelClick };
}
