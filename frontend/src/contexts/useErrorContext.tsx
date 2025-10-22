import { createContext, ReactNode, useContext, useState } from 'react';

type ErrorContextType = {
  appError: Error | null;
  updateAppError: (error: Error | null) => void;
};

const ErrorContext = createContext<ErrorContextType | undefined>(undefined);

export const ErrorProvider = ({ children }: { children: ReactNode }) => {
  const [appError, setAppError] = useState<Error | null>(null);

  const updateAppError = (error: Error | null) => {
    setAppError(error);
  };

  return (
    <ErrorContext.Provider value={{ appError, updateAppError }}>
      {children}
    </ErrorContext.Provider>
  );
};

export const useErrorContext = (): ErrorContextType => {
  const context = useContext(ErrorContext);
  if (!context) {
    throw new Error('useErrorContext must be used within an ErrorProvider');
  }
  return context;
};
