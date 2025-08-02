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
  const [isLoading, setIsLoading] = useState(false);
  const [isComplete, setIsComplete] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsLoading(true);
      setIsComplete(false);

      const loadingTimer = setTimeout(() => {
        setIsLoading(false);
        setIsComplete(true);
      }, loadingDuration);

      return () => {
        clearTimeout(loadingTimer);
      };
    }
  }, [isOpen, loadingDuration]);

  useEffect(() => {
    if (isComplete) {
      const autoCloseTimer = setTimeout(() => {
        onClose();
      }, autoCloseDuration);

      return () => {
        clearTimeout(autoCloseTimer);
      };
    }
  }, [isComplete, autoCloseDuration, onClose]);

  const handleModalClose = useCallback(() => {
    if (!isLoading) {
      onClose();
    }
  }, [isLoading, onClose]);

  return {
    isLoading,
    isComplete,
    handleModalClose,
  };
}
