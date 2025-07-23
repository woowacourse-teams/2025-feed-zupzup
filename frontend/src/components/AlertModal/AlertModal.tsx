import Modal from '@/components/Modal/Modal';
import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { content, title, message, buttonContainer } from './AlertModal.styles';

export interface AlertModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
}

export default function AlertModal({
  isOpen,
  onClose,
  title: alertTitle,
  message: alertMessage,
  onConfirm,
  confirmText = '확인',
}: AlertModalProps) {
  const theme = useAppTheme();

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div css={content}>
        <p css={title(theme)}>{alertTitle}</p>
        {alertMessage && <p css={message(theme)}>{alertMessage}</p>}
      </div>
      <div css={buttonContainer}>
        <BasicButton variant='primary' width='100%' onClick={handleConfirm}>
          {confirmText}
        </BasicButton>
      </div>
    </Modal>
  );
}
