import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';

export interface UseFeedbackFormReturn {
  feedback: string;
  username: string;
  isLocked: boolean;
  currentAvatar: string;

  handleFeedbackChange: (event: React.ChangeEvent<HTMLTextAreaElement>) => void;
  handleRandomChange: () => void;
  handleLockToggle: () => void;
  resetForm: () => void;

  canSubmit: boolean;
  handleSubmit: () => void;
}

export function useFeedbackForm(): UseFeedbackFormReturn {
  const [feedback, setFeedback] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK
  );
  const [username, setUsername] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.USERNAME
  );
  const [isLocked, setIsLocked] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.IS_LOCKED
  );
  const [currentAvatar, setCurrentAvatar] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.AVATAR
  );

  const handleFeedbackChange = useCallback(
    (event: React.ChangeEvent<HTMLTextAreaElement>) => {
      setFeedback(event.target.value as typeof feedback);
    },
    []
  );

  const handleRandomChange = useCallback(() => {
    if (isLocked) return;

    const newUsername = FEEDBACK_INPUT_CONSTANTS.generateRandomUsername();
    setUsername(newUsername as typeof username);

    const randomAvatarIndex = Math.floor(
      Math.random() * FEEDBACK_INPUT_CONSTANTS.AVATARS.length
    );
    setCurrentAvatar(FEEDBACK_INPUT_CONSTANTS.AVATARS[randomAvatarIndex]);
  }, [isLocked]);

  const handleLockToggle = useCallback(() => {
    setIsLocked((prev) => !prev as typeof isLocked);
  }, []);

  const resetForm = useCallback(() => {
    setFeedback(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK);
    setUsername(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.USERNAME);
    setIsLocked(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.IS_LOCKED);
    setCurrentAvatar(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.AVATAR);
  }, []);

  const canSubmit = feedback.trim().length > 0;

  const handleSubmit = useCallback(() => {
    console.log('submit', feedback, username, isLocked, currentAvatar);
  }, [feedback, username, isLocked, currentAvatar]);

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
  };
}
