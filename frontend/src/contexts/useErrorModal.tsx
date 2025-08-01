import { ApiError } from '@/apis/apiClient';
import {
  createContext,
  Dispatch,
  SetStateAction,
  useCallback,
  useContext,
  useState,
} from 'react';

interface ErrorModalContextProps {
  isError: boolean;
  title: string;
  message: string;
  setErrorTrue: Dispatch<SetStateAction<string>>;
  setErrorFalse: Dispatch<SetStateAction<string>>;
  showErrorModal: (error: unknown, title?: string) => void;
}

const errorModalContext = createContext<ErrorModalContextProps>({
  isError: false,
  title: '',
  message: '',
  setErrorTrue: () => {},
  setErrorFalse: () => {},
  showErrorModal: () => {},
});

interface ErrorModalProvider {
  children: React.ReactNode;
}

export function ErrorModalProvider({ children }: ErrorModalProvider) {
  const [isError, setIsError] = useState(false);
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');

  const setErrorTrue = useCallback(() => {
    setIsError(true);
  }, []);

  const setErrorFalse = useCallback(() => {
    setIsError(false);
  }, []);

  const showErrorModal = useCallback((error: unknown, title?: string) => {
    if (error instanceof ApiError) {
      setMessage(error.message);
      setTitle(title ?? '에러 발생');
      setErrorTrue();
    }
  }, []);
  return (
    <errorModalContext.Provider
      value={{
        isError,
        title,
        message,
        setErrorTrue,
        setErrorFalse,
        showErrorModal,
      }}
    >
      {children}
    </errorModalContext.Provider>
  );
}

export function useErrorModalContext() {
  const context = useContext(errorModalContext);
  if (!context) {
    throw new Error('컨텍스트는 CouponProvider 내부에서 사용해야 합니다.');
  }
  return context;
}
