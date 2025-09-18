import { useCallback } from 'react';
import { useFeedbackInput } from './useFeedbackInput';
import { useUsername } from './useUsername';
import { useLockState } from './useLockState';

export interface UseFeedbackFormReturn {
  feedback: string;
  username: string;
  isLocked: boolean;

  handleFeedbackChange: (value: string) => void;
  handleUsernameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleUsernameFocus: () => void;
  handleRandomChange: () => void;
  handleLockToggle: () => void;
  resetForm: () => void;

  canSubmit: boolean;
}

export function useFeedbackForm(): UseFeedbackFormReturn {
  const { isLocked, handleLockToggle, resetLockState } = useLockState();
  const { feedback, handleFeedbackChange, resetFeedback } = useFeedbackInput();
  const {
    username,
    handleRandomChange,
    handleUsernameChange,
    handleUsernameFocus,
    resetUsername,
  } = useUsername();

  const canSubmit = feedback.trim().length > 0;

  const resetForm = useCallback(() => {
    resetFeedback();
    resetUsername();
    resetLockState();
  }, [resetFeedback, resetUsername, resetLockState]);

  return {
    feedback,
    username,
    isLocked,

    handleFeedbackChange,
    handleUsernameChange,
    handleUsernameFocus,
    handleRandomChange,
    handleLockToggle,
    resetForm,

    canSubmit,
  };
}
