import { useEffect, useState } from 'react';

export default function useHighLighted() {
  const storageHighlightedId = localStorage.getItem('highlightedId');

  const [highlightedId, setHighLightedId] = useState<number | null>(null);

  useEffect(() => {
    if (!storageHighlightedId) return;

    setHighLightedId(Number(storageHighlightedId));

    const timeout = setTimeout(() => {
      setHighLightedId(null);
      localStorage.removeItem('highlightedId');
    }, 2000);

    return () => clearTimeout(timeout);
  }, []);

  return { highlightedId };
}
