import React, { createContext, useContext, useState } from 'react';

interface PWAPromptContextType {
  isShown: boolean;
  showPrompt: () => void;
  hidePrompt: () => void;
}

const PWAPromptContext = createContext<PWAPromptContextType | undefined>(
  undefined
);

export const PWAPromptProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [isShown, setIsShown] = useState(false);

  const showPrompt = () => setIsShown(true);
  const hidePrompt = () => setIsShown(false);

  return (
    <PWAPromptContext.Provider value={{ isShown, showPrompt, hidePrompt }}>
      {children}
    </PWAPromptContext.Provider>
  );
};

export const usePWAPrompt = () => {
  const context = useContext(PWAPromptContext);
  if (!context) {
    throw new Error(
      'usePWAPrompt는 PWAPromptProvider 내부에서 사용되어야 합니다'
    );
  }
  return context;
};
