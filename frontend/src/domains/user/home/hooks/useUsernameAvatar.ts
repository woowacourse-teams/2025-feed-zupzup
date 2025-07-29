import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';
import {
  generateRandomAvatar,
  generateRandomUsername,
} from '../utils/feedbackUtils';

export interface UseUsernameAvatarReturn {
  username: string;
  currentAvatar: string;
  handleRandomChange: () => void;
  resetUsernameAvatar: () => void;
}

export function useUsernameAvatar(isLocked: boolean): UseUsernameAvatarReturn {
  const [username, setUsername] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.USERNAME
  );
  const [currentAvatar, setCurrentAvatar] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.AVATAR
  );

  const handleRandomChange = useCallback(() => {
    if (isLocked) return;

    const newUsername = generateRandomUsername();
    setUsername(newUsername as typeof username);

    const newAvatar = generateRandomAvatar();
    setCurrentAvatar(newAvatar);
  }, [isLocked]);

  const resetUsernameAvatar = useCallback(() => {
    setUsername(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.USERNAME);
    setCurrentAvatar(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.AVATAR);
  }, []);

  return {
    username,
    currentAvatar,
    handleRandomChange,
    resetUsernameAvatar,
  };
}
