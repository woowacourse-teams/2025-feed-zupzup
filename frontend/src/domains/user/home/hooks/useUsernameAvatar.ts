import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';

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

    const newUsername = FEEDBACK_INPUT_CONSTANTS.generateRandomUsername();
    setUsername(newUsername as typeof username);

    const randomAvatarIndex = Math.floor(
      Math.random() * FEEDBACK_INPUT_CONSTANTS.AVATARS.length
    );
    setCurrentAvatar(FEEDBACK_INPUT_CONSTANTS.AVATARS[randomAvatarIndex]);
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
