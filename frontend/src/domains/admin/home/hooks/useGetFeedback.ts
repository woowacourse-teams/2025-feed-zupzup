import { useEffect } from 'react';
import useIntersectionObserve from '@/hooks/useIntersectionObserve';

interface UseIntersectionObserveProps {
  fetchMore: () => void;
  hasNext: boolean;
}

export default function useGetFeedback({
  fetchMore,
  hasNext,
}: UseIntersectionObserveProps) {
  useEffect(() => {
    fetchMore();
  }, []);

  const { observe, unobserve } = useIntersectionObserve();

  useEffect(() => {
    const observerElement = document.getElementById('scroll-observer');
    if (!hasNext && observerElement) {
      unobserve(observerElement);
      return;
    }

    if (observerElement) {
      observe(observerElement, ([entry]) => {
        if (entry.isIntersecting) {
          fetchMore();
        }
      });
    }

    return () => {
      if (observerElement) {
        unobserve(observerElement);
      }
    };
  }, [fetchMore, observe, unobserve]);
}
