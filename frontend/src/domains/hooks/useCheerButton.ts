import { postOrganizationCheer } from '@/apis/organization.api';
import { useDebounce } from '@/hooks/useDebounce';
import { useState } from 'react';

interface UseCheerButtonReturn {
  handleClick: () => void;
  accCount: number;
  animate: boolean;
}

export default function useCheerButton(): UseCheerButtonReturn {
  const [count, setCount] = useState(0);
  const [accCount, setAccCount] = useState(0);
  const [animate, setAnimate] = useState(false);

  const handleClick = () => {
    setCount(count + 1);
    setAccCount(accCount + 1);
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
    handleClick,
    accCount,
    animate,
  };
}
