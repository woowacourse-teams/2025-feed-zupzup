import { useEffect } from 'react';

export default function useGoTop() {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
}
