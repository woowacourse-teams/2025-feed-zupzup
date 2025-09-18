import { createContext, useCallback, useContext, useState } from 'react';
import { createPortal } from 'react-dom';
import Toast from '@/domains/components/Toast/Toast'; // Toast 컴포넌트 경로에 맞게 수정해주세요

interface ToastContextProps {
  showToast: (message: string, duration?: number) => void;
  hideToast: () => void;
}

const toastContext = createContext<ToastContextProps>({
  showToast: () => {},
  hideToast: () => {},
});

interface ToastProviderProps {
  children: React.ReactNode;
}

export interface ToastData {
  message: string;
  duration: number;
}

export function ToastProvider({ children }: ToastProviderProps) {
  const [toastData, setToastData] = useState<ToastData | null>(null);
  const portalTarget = document.getElementById('toast') || document.body;
  const showToast = useCallback((message: string, duration: number = 3000) => {
    setToastData({ message, duration });
  }, []);

  const hideToast = useCallback(() => {
    setToastData(null);
  }, []);

  return (
    <toastContext.Provider
      value={{
        showToast,
        hideToast,
      }}
    >
      {children}
      {toastData &&
        createPortal(
          <Toast
            message={toastData.message}
            onClose={hideToast}
            duration={toastData.duration}
          />,
          portalTarget
        )}
    </toastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(toastContext);
  if (!context) {
    throw new Error('컨텍스트는 ToastProvider 내부에서 사용해야 합니다.');
  }
  return context;
}
