import { postOrganizationCheer } from '@/apis/organization.api';
import { useDebounce } from '@/hooks/useDebounce';
import { useToastActions } from '@/stores/Toast/useToast';
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
  const { showToast } = useToastActions();

  const handleCheerButton = () => {
    if (isDisabled) {
      return;
    }

    const newCount = count + 1;
    const MAX_CHEER_COUNT = 100;
    const DISABLE_DURATION = 5000;

    setCount(newCount);
    setAnimate(false);
    requestAnimationFrame(() => setAnimate(true));

    if (newCount >= MAX_CHEER_COUNT) {
      showToast(
        '응원은 한 번에 100개까지만 가능해요! 잠시 후 다시 시도해주세요.'
      );
      setIsDisabled(true);

      setTimeout(() => {
        setIsDisabled(false);
      }, DISABLE_DURATION);
    }

    const countToSend = Math.min(newCount, MAX_CHEER_COUNT);
    debouncedSearch(countToSend);
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
