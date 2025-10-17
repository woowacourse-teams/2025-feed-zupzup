import {
  iconContainer,
  toastStyle,
} from '@/domains/components/Toast/Toast.style';
import ToastDangerIcon from '@/components/icons/ToastDangerIcon';
import { useEffect, useState } from 'react';
import ToastCheckIcon from '@/components/icons/ToastCheckIcon';
import { ToastType } from '@/contexts/useToast';
import { useToastActions } from '@/stores/Toast/useToast';

interface ToastProps {
  type: ToastType;
  message: string;
  duration?: number;
}

export default function Toast({
  type = 'error',
  message,
  duration = 3000,
}: ToastProps) {
  const { hideToast } = useToastActions();
  const [isExiting, setIsExiting] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsExiting(true);
      setTimeout(() => {
        hideToast();
      }, 300);
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, hideToast]);

  return (
    <div css={toastStyle(isExiting)}>
      <p css={iconContainer}>
        {type === 'error' && <ToastDangerIcon />}
        {type === 'success' && <ToastCheckIcon />}
      </p>
      <span>{message}</span>
    </div>
  );
}
