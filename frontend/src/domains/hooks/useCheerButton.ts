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
    (async (count: number) => {
      try {
        await postOrganizationCheer({
          organizationId,
          cheeringCount: count,
        });
        setCount(0);
      } catch (err) {
        console.error(err);
        showToast('응원 전송에 실패했습니다. 다시 시도해주세요.');
      }
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
