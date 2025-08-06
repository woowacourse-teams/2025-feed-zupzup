import { useState, useEffect, useCallback } from 'react';

export interface UseTimeDelayModalOptions {
  isOpen: boolean;
  loadingDuration: number;
  autoCloseDuration: number;
  onClose: () => void;
}

export function useTimeDelayModal({
  isOpen,
  loadingDuration,
  autoCloseDuration,
  onClose,
}: UseTimeDelayModalOptions) {
  const [isMinimumDelayActive, setIsMinimumDelayActive] = useState(false);
  const [isComplete, setIsComplete] = useState(false);

  useEffect(() => {
    if (!isOpen) return;

    setIsMinimumDelayActive(true);
    setIsComplete(false);

    const delayTimer = setTimeout(() => {
      setIsMinimumDelayActive(false);
      setIsComplete(true);
    }, loadingDuration);

    return () => {
      clearTimeout(delayTimer);
    };
  }, [isOpen, loadingDuration]);

  useEffect(() => {
    if (!isComplete) return;

    const autoCloseTimer = setTimeout(() => {
      onClose();
    }, autoCloseDuration);

    return () => {
      clearTimeout(autoCloseTimer);
    };
  }, [isComplete, autoCloseDuration, onClose]);

  const handleModalClose = useCallback(() => {
    if (!isMinimumDelayActive) {
      onClose();
    }
  }, [isMinimumDelayActive, onClose]);

  return {
    isMinimumDelayActive,
    isComplete,
    handleModalClose,
  };
}
