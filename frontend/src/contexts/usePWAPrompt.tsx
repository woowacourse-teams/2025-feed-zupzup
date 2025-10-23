import React, { createContext, useContext, useState, useEffect } from 'react';

interface PWAPromptContextType {
  isShown: boolean;
  showPrompt: () => void;
  hidePrompt: () => void;
}

const PWAPromptContext = createContext<PWAPromptContextType | undefined>(
  undefined
);

const isPWAInstallable = () => {
  if (window.matchMedia('(display-mode: standalone)').matches) {
    return false;
  }

  const isIOS = /iphone|ipad|ipod/.test(
    window.navigator.userAgent.toLowerCase()
  );
  const isSafari =
    /safari/.test(window.navigator.userAgent.toLowerCase()) &&
    !/chrome/.test(window.navigator.userAgent.toLowerCase());

  if (isIOS && isSafari) {
    return true;
  }

  if ('serviceWorker' in navigator && 'PushManager' in window) {
    return true;
  }

  return false;
};

const hasShownPWAInstallPrompt = () => {
  return localStorage.getItem('pwa-install-prompt-shown') === 'true';
};

const setPWAInstallPromptShown = () => {
  localStorage.setItem('pwa-install-prompt-shown', 'true');
};

export const PWAPromptProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [isShown, setIsShown] = useState(false);

  const showPrompt = () => setIsShown(true);
  const hidePrompt = () => setIsShown(false);

  useEffect(() => {
    const shouldShowPrompt = () => {
      if (!isPWAInstallable()) {
        return false;
      }

      if (hasShownPWAInstallPrompt()) {
        return false;
      }

      return true;
    };

    if (shouldShowPrompt()) {
      const timer = setTimeout(() => {
        setIsShown(true);
        setPWAInstallPromptShown();
      }, 1000);

      return () => clearTimeout(timer);
    }
  }, []);

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
