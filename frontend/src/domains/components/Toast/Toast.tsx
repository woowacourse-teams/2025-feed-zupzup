import { toastStyle } from '@/domains/components/Toast/Toast.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useEffect } from 'react';

interface ToastProps {
  message: string;
  onClose: () => void;
  duration?: number;
}

export default function Toast({
  message,
  onClose,
  duration = 3000,
}: ToastProps) {
  const theme = useAppTheme();
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  return <div css={toastStyle(theme)}>{message}</div>;
}
