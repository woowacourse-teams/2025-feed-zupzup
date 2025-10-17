import { createPortal } from 'react-dom';
import { useToastState } from './useToast';
import Toast from '@/domains/components/Toast/Toast';

export default function ToastProvider() {
  const { toastData } = useToastState();
  const portalTarget = document.getElementById('toast') || document.body;

  return (
    <>
      {toastData &&
        createPortal(
          <Toast
            type={toastData.type}
            message={toastData.message}
            duration={toastData.duration}
          />,
          portalTarget
        )}
    </>
  );
}
