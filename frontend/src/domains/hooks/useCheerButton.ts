import { postOrganizationCheer } from '@/apis/organization.api';
import { useDebounce } from '@/hooks/useDebounce';
import { useState } from 'react';

interface UseCheerButtonProps {
  organizationId: string;
}

export default function useCheerButton({
  organizationId,
}: UseCheerButtonProps) {
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
        organizationId,
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
