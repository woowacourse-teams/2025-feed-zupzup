import { useEffect, useRef } from 'react';

interface UseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm?: (() => void) | undefined;
  preventClose?: boolean;
}

export const useModal = ({
  isOpen,
  onClose,
  onConfirm,
  preventClose = false,
}: UseModalProps) => {
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !preventClose) {
        onCloseRef.current();
      }
    };

    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, preventClose]);

  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget && !preventClose) {
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
