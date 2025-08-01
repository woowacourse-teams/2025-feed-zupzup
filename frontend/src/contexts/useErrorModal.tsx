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
  setTitle: Dispatch<SetStateAction<string>>;
  setMessage: Dispatch<SetStateAction<string>>;
  setErrorTrue: () => void;
  setErrorFalse: () => void;
}

const errorModalContext = createContext<ErrorModalContextProps>({
  isError: false,
  title: '',
  message: '',
  setTitle: () => {},
  setMessage: () => {},
  setErrorTrue: () => {},
  setErrorFalse: () => {},
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

  return (
    <errorModalContext.Provider
      value={{
        isError,
        title,
        message,
        setErrorTrue,
        setErrorFalse,
        setTitle,
        setMessage,
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
