import { SerializedStyles } from '@emotion/react';
import { useState, useEffect, useCallback } from 'react';
import { useAppTheme } from '@/hooks/useAppTheme';
import Modal from '@/components/Modal/Modal';
import {
  loadingContainer,
  completeContainer,
  spinner,
  checkIcon,
  messageText,
} from '@/components/TimeDelayModal/TimeDelayModal.styles';

export interface TimeDelayModalProps {
  isOpen: boolean;
  onClose: () => void;
  loadingDuration?: number;
  autoCloseDuration?: number;
  loadingMessage?: string;
  completeMessage?: string;
  width?: number;
  height?: number;
  customCSS?: SerializedStyles | SerializedStyles[];
}

export default function TimeDelayModal({
  isOpen,
  onClose,
  loadingDuration = 1000,
  autoCloseDuration = 2000,
  loadingMessage = '피드백을 전송하고 있어요...',
  completeMessage = '소중한 의견 감사해요!',
  width = 300,
  height,
  customCSS,
}: TimeDelayModalProps) {
  const theme = useAppTheme();
  const [isLoading, setIsLoading] = useState(false);
  const [isComplete, setIsComplete] = useState(false);

  console.log('🔄 TimeDelayModal 렌더링:', {
    isOpen,
    isLoading,
    isComplete,
    timestamp: Date.now(),
  });

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

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleModalClose}
      width={width}
      height={height}
      {...(customCSS && { customCSS })}
    >
      {isLoading ? (
        <div css={loadingContainer}>
          <div css={spinner} />
          <p css={messageText(theme)}>{loadingMessage}</p>
        </div>
      ) : (
        <div css={completeContainer}>
          <div css={checkIcon}>✓</div>
          <p css={messageText(theme)}>{completeMessage}</p>
        </div>
      )}
    </Modal>
  );
}
