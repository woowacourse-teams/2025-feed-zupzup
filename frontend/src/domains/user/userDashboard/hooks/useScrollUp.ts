import { useCallback, useEffect, useState } from 'react';

const SCROLL_THRESHOLD = 200;

export default function useScrollUp() {
  const [showButton, setShowButton] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      const shouldShow = window.scrollY > SCROLL_THRESHOLD;
      setShowButton(shouldShow);
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToTop = useCallback(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, []);

  return { showButton, scrollToTop };
}
