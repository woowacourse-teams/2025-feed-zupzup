import { useCallback, useEffect, useRef } from 'react';

const basicOptions: IntersectionObserverInit = {
  root: null,
  rootMargin: '0px',
  threshold: 0.5,
};

export default function useIntersectionObserve() {
  const observerRef = useRef<IntersectionObserver | null>(null);

  const observe = useCallback(
    (
      element: Element,
      callback: IntersectionObserverCallback,
      options: IntersectionObserverInit = basicOptions
    ) => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }

      observerRef.current = new IntersectionObserver(callback, options);
      observerRef.current.observe(element);
    },
    []
  );

  const unobserve = useCallback((element: Element) => {
    if (observerRef.current) {
      observerRef.current.unobserve(element);
    }
  }, []);

  useEffect(() => {
    return () => {
      observerRef.current?.disconnect();
    };
  }, []);

  return { observe, unobserve };
}
