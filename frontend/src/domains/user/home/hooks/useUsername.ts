import { useState } from 'react';
import { generateRandomUsername } from '../utils/feedbackUtils';

export interface UseUsernameReturn {
  username: string;
  resetUsername: () => void;
  handleRandomChange: () => void;
  handleUsernameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleUsernameFocus: () => void;
}

export function useUsername(): UseUsernameReturn {
  const [username, setUsername] = useState(() => generateRandomUsername());

  const [isUsernameEdited, setIsUsernameEdited] = useState(false);

  const handleRandomChange = () => {
    const newUsername = generateRandomUsername();
    setUsername(newUsername as typeof username);

    setIsUsernameEdited(false);
  };

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value as typeof username);
    setIsUsernameEdited(true);
  };

  const handleUsernameFocus = () => {
    if (!isUsernameEdited) {
      setUsername('' as typeof username);
      setIsUsernameEdited(true);
    }
  };

  const resetUsername = () => {
    setUsername(generateRandomUsername());
    setIsUsernameEdited(false);
  };

  return {
    username,
    resetUsername,
    handleRandomChange,
    handleUsernameChange,
    handleUsernameFocus,
  };
}
