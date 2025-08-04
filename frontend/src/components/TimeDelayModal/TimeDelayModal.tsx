import { SerializedStyles } from '@emotion/react';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useTimeDelayModal } from '@/hooks/useTimeDelayModal';
import Modal from '@/components/Modal/Modal';
import {
  loadingContainer,
  completeContainer,
  spinner,
  checkIcon,
  messageText,
  modalContainer,
  errorIcon,
} from '@/components/TimeDelayModal/TimeDelayModal.styles';
import { StatusType } from '@/types/status.types';

export interface TimeDelayModalProps {
  isOpen: boolean;
  onClose: () => void;
  loadingDuration?: number;
  autoCloseDuration?: number;
  loadingMessage?: string;
  completeMessage?: string;
  errorMessage?: string;
  modalStatus?: StatusType;
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
  errorMessage = '다시 시도해 주세요.',
  width = 300,
  height,
  customCSS,
  modalStatus,
}: TimeDelayModalProps) {
  const theme = useAppTheme();

  const { isMinimumDelayActive, handleModalClose } = useTimeDelayModal({
    isOpen,
    loadingDuration,
    autoCloseDuration,
    onClose,
  });

  const renderContent = () => {
    const isApiSubmitting = modalStatus === 'submitting';

    if (isApiSubmitting || isMinimumDelayActive) {
      return (
        <div css={loadingContainer}>
          <div css={spinner(theme)} />
          <p css={messageText(theme)}>{loadingMessage}</p>
        </div>
      );
    }

    if (modalStatus === 'error') {
      return (
        <div css={completeContainer}>
          <div css={errorIcon(theme)}>❌</div>
          <p css={messageText(theme)}>{errorMessage}</p>
        </div>
      );
    }

    return (
      <div css={completeContainer}>
        <div css={checkIcon(theme)}>✅</div>
        <p css={messageText(theme)}>{completeMessage}</p>
      </div>
    );
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleModalClose}
      width={width}
      height={height}
      customCSS={customCSS || modalContainer}
    >
      {renderContent()}
    </Modal>
  );
}
