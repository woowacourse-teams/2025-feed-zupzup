import { toastStyle } from '@/domains/components/Toast/Toast.style';
import Danger from '@/components/icons/Danger';
import { useEffect, useState } from 'react';

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
  const [isExiting, setIsExiting] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsExiting(true);
      setTimeout(() => {
        onClose();
      }, 300);
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  return (
    <div css={toastStyle(isExiting)}>
      <Danger />
      <span>{message}</span>
    </div>
  );
}
