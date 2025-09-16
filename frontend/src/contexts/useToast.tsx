import { createContext, useCallback, useContext, useState } from 'react';

interface ToastContextProps {
  toastData: ToastData | null;
  showToast: (message: string, duration?: number) => void;
  hideToast: () => void;
}

const toastContext = createContext<ToastContextProps>({
  toastData: null,
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

  const showToast = useCallback((message: string, duration: number = 3000) => {
    setToastData({ message, duration });
  }, []);

  const hideToast = useCallback(() => {
    setToastData(null);
  }, []);

  return (
    <toastContext.Provider
      value={{
        toastData,
        showToast,
        hideToast,
      }}
    >
      {children}
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
