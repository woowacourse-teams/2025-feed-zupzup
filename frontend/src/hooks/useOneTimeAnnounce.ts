import { useEffect, useState } from 'react';

export function useOneTimeAnnounce(delay = 3000) {
  const [isInitial, setIsInitial] = useState(true);

  useEffect(() => {
    const t = setTimeout(() => setIsInitial(false), delay);
    return () => clearTimeout(t);
  }, [delay]);

  return isInitial;
}
