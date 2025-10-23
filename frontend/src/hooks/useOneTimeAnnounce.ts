import { useEffect, useState } from 'react';

export function useInitialAnnounceWindow(delay = 3000) {
  const [isInitial, setIsInitial] = useState(true);

  useEffect(() => {
    const t = setTimeout(() => setIsInitial(false), delay);
    return () => clearTimeout(t);
  }, [delay]);

  return isInitial;
}
