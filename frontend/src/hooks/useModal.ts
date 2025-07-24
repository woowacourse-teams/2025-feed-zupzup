import { useEffect } from 'react';

interface UseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm?: (() => void) | undefined;
}

export const useModal = ({ isOpen, onClose, onConfirm }: UseModalProps) => {
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
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
