import { postOrganizationCheer } from '@/apis/organization.api';
import { useToast } from '@/contexts/useToast';
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
  const [isDisabled, setIsDisabled] = useState(false);
  const { showToast } = useToast();

  const handleCheerButton = () => {
    if (isDisabled) {
      return;
    }
    setCount(count + 1);
    setAnimate(false);
    requestAnimationFrame(() => setAnimate(true));

    const newCount = count + 1;

    if (newCount === 100) {
      showToast(
        '응원은 한 번에 100개까지만 가능해요! 잠시 후 다시 시도해주세요.'
      );
      setIsDisabled(true);

      setTimeout(() => {
        setIsDisabled(false);
      }, 5000);
    }

    debouncedSearch(newCount);
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
    isDisabled,
  };
}
