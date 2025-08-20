import { useEffect, useRef } from 'react';

interface UseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm?: (() => void) | undefined;
  disableUserClose?: boolean;
}

export const useModal = ({
  isOpen,
  onClose,
  onConfirm,
  disableUserClose = false,
}: UseModalProps) => {
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !disableUserClose) {
        onCloseRef.current();
      }
    };

    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, disableUserClose]);

  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget && !disableUserClose) {
      onClose();
    }
  };

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  return {
    handleOverlayClick,
    handleConfirm,
  };
};
