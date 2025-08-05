import { getLocalStorage } from '@/utils/localStorage';
import { useEffect, useState } from 'react';

export default function useMyFeedbacks() {
  const [myFeedbacks, setMyFeedbacks] = useState<number[]>([]);

  const isMyFeedback = (feedbackId: number) => {
    return myFeedbacks.includes(feedbackId);
  };

  useEffect(() => {
    const storedFeedbacks = getLocalStorage<number[]>('myFeedbacks') || [];
    setMyFeedbacks(storedFeedbacks);
  }, []);

  return {
    isMyFeedback,
  };
}
