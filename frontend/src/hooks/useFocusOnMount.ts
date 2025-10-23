import { useEffect } from 'react';

export default function useFocusOnMount({
  ref,
  targetId,
  delay = 350,
}: {
  ref: React.RefObject<HTMLElement>;
  targetId: string;
  delay?: number;
}) {
  useEffect(() => {
    const t = setTimeout(() => {
      const el =
        ref.current ??
        (document.getElementById(targetId) as HTMLTextAreaElement | null);
      if (!el) return;
      el.focus({ preventScroll: true });
      const reduce = window.matchMedia?.(
        '(prefers-reduced-motion: reduce)'
      ).matches;
      el.scrollIntoView({
        block: 'start',
        behavior: reduce ? 'auto' : 'smooth',
      });
    }, delay);
    return () => clearTimeout(t);
  }, []);
}
