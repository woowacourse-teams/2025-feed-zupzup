import { useEffect } from 'react';
import useIntersectionObserve from '@/hooks/useIntersectionObserve';

interface UseIntersectionObserveProps {
  fetchMore: () => void;
  hasNext: boolean;
  loading: boolean;
}

export default function useGetFeedback({
  fetchMore,
  hasNext,
  loading,
}: UseIntersectionObserveProps) {
  const { observe, unobserve } = useIntersectionObserve();

  useEffect(() => {
    if (!hasNext || loading) return;
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
  }, [fetchMore, observe, unobserve, hasNext, loading]);
}
