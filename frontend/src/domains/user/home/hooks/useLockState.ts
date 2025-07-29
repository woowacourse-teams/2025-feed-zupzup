import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';

export interface UseLockStateReturn {
  isLocked: boolean;
  handleLockToggle: () => void;
  resetLockState: () => void;
}

export function useLockState(): UseLockStateReturn {
  const [isLocked, setIsLocked] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.IS_LOCKED
  );

  const handleLockToggle = useCallback(() => {
    setIsLocked((prev) => !prev as typeof isLocked);
  }, []);

  const resetLockState = useCallback(() => {
    setIsLocked(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.IS_LOCKED);
  }, []);

  return {
    isLocked,
    handleLockToggle,
    resetLockState,
  };
}
