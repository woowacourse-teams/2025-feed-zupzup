import Modal from '@/components/Modal/Modal';
import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  content,
  title,
  message,
  buttonContainer,
} from './ConfirmModal.styles';

export interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
  width?: number;
  height?: number;
}

export default function ConfirmModal({
  isOpen,
  onClose,
  title: confirmTitle,
  message: confirmMessage,
  onConfirm,
  confirmText = '확인',
  cancelText = '취소',
  width = 300,
  height,
}: ConfirmModalProps) {
  const theme = useAppTheme();

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} width={width} height={height}>
      <div css={content}>
        <p css={title(theme)}>{confirmTitle}</p>
        {confirmMessage && <p css={message(theme)}>{confirmMessage}</p>}
      </div>
      <div css={buttonContainer}>
        <BasicButton
          variant='secondary'
          width='calc(50% - 12px)'
          onClick={onClose}
        >
          {cancelText}
        </BasicButton>
        <BasicButton
          variant='primary'
          width='calc(50% - 12px)'
          onClick={handleConfirm}
        >
          {confirmText}
        </BasicButton>
      </div>
    </Modal>
  );
}
