import { postOrganizationCheer } from '@/apis/organization.api';
import { useDebounce } from '@/hooks/useDebounce';
import { useState } from 'react';

export default function useCheerButton() {
  const [count, setCount] = useState(0);
  const [animate, setAnimate] = useState(false);

  const handleCheerButton = () => {
    setCount(count + 1);
    setAnimate(false);
    requestAnimationFrame(() => setAnimate(true));
    debouncedSearch(count + 1);
  };

  const debouncedSearch = useDebounce(
    ((count: number) => {
      postOrganizationCheer({
        organizationId: 1,
        cheeringCount: count,
      });
      setCount(0);
    }) as (...args: unknown[]) => void,
    500
  );

  return {
    count,
    animate,
    handleCheerButton,
  };
}
