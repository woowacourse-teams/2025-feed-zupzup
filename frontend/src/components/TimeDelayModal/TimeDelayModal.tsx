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

  const { isLoading, handleModalClose } = useTimeDelayModal({
    isOpen,
    loadingDuration,
    autoCloseDuration,
    onClose,
  });

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleModalClose}
      width={width}
      height={height}
      customCSS={customCSS || modalContainer}
    >
      {isLoading ? (
        <div css={loadingContainer}>
          <div css={spinner(theme)} />
          <p css={messageText(theme)}>{loadingMessage}</p>
        </div>
      ) : (
        <div css={completeContainer}>
          <div css={checkIcon(theme)}>✅</div>
          <p css={messageText(theme)}>{completeMessage}</p>
        </div>
      )}
    </Modal>
  );
}
