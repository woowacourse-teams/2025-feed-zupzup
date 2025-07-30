import { useCallback } from 'react';
import { useFeedbackInput } from './useFeedbackInput';
import { useUsernameAvatar } from './useUsernameAvatar';
import { useLockState } from './useLockState';

export interface UseFeedbackFormReturn {
  feedback: string;
  username: string;
  isLocked: boolean;
  currentAvatar: string;

  handleFeedbackChange: (value: string) => void;
  handleRandomChange: () => void;
  handleLockToggle: () => void;
  resetForm: () => void;

  canSubmit: boolean;
  handleSubmit: () => void;
  handleFormSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
}

export function useFeedbackForm(): UseFeedbackFormReturn {
  const { isLocked, handleLockToggle, resetLockState } = useLockState();
  const { feedback, handleFeedbackChange, resetFeedback } = useFeedbackInput();
  const { username, currentAvatar, handleRandomChange, resetUsernameAvatar } =
    useUsernameAvatar(isLocked);

  const canSubmit = feedback.trim().length > 0;

  const handleSubmit = useCallback(() => {
    if (!canSubmit) return;
  }, [feedback, username, isLocked, currentAvatar, canSubmit]);

  const handleFormSubmit = useCallback(
    (event: React.FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      handleSubmit();
    },
    [handleSubmit]
  );

  const resetForm = useCallback(() => {
    resetFeedback();
    resetUsernameAvatar();
    resetLockState();
  }, [resetFeedback, resetUsernameAvatar, resetLockState]);

  return {
    feedback,
    username,
    isLocked,
    currentAvatar,

    handleFeedbackChange,
    handleRandomChange,
    handleLockToggle,
    resetForm,

    canSubmit,
    handleSubmit,
    handleFormSubmit,
  };
}
