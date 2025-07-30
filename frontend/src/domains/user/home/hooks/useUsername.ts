import { useState, useCallback } from 'react';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import { generateRandomUsername } from '../utils/feedbackUtils';

export interface UseUsernameReturn {
  username: string;
  resetUsername: () => void;
  handleRandomChange: () => void;
  handleUsernameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleUsernameFocus: () => void;
}

export function useUsername(isLocked: boolean): UseUsernameReturn {
  const [username, setUsername] = useState(
    FEEDBACK_FORM_CONSTANTS.DEFAULTS.USERNAME
  );

  const [isUsernameEdited, setIsUsernameEdited] = useState(false);

  const handleRandomChange = useCallback(() => {
    if (isLocked) return;

    const newUsername = generateRandomUsername();
    setUsername(newUsername as typeof username);

    setIsUsernameEdited(false);
  }, [isLocked]);

  const handleUsernameChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setUsername(e.target.value as typeof username);
      setIsUsernameEdited(true);
    },
    []
  );

  const handleUsernameFocus = useCallback(() => {
    if (!isUsernameEdited) {
      setUsername('' as typeof username);
      setIsUsernameEdited(true);
    }
  }, [isUsernameEdited]);

  const resetUsername = useCallback(() => {
    setUsername(FEEDBACK_FORM_CONSTANTS.DEFAULTS.USERNAME);
    setIsUsernameEdited(false);
  }, []);

  return {
    username,
    resetUsername,
    handleRandomChange,
    handleUsernameChange,
    handleUsernameFocus,
  };
}
