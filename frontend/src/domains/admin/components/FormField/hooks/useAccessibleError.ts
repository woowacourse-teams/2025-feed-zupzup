import { useEffect, useState } from 'react';

export function useAccessibleError(
  value: string,
  errorMessage: string,
  touched: boolean
) {
  const [displayError, setDisplayError] = useState('');

  useEffect(() => {
    if (touched && errorMessage) {
      setDisplayError('');
      const timer = setTimeout(() => {
        setDisplayError(errorMessage);
      }, 10);
      return () => clearTimeout(timer);
    } else if (touched) {
      setDisplayError(' ');
    }
    return undefined;
  }, [value, errorMessage, touched]);

  return displayError;
}
