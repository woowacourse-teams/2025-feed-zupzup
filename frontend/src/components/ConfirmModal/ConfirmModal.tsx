import Modal from '@/components/Modal/Modal';
import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  content,
  title,
  message,
  buttonContainer,
} from '@/components/Modal/Modal.styles';

export interface ConfirmModalProps {
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
  width?: number;
  height?: number;
  disabled?: boolean;
}

export default function ConfirmModal({
  onClose,
  title: confirmTitle,
  message: confirmMessage,
  onConfirm,
  confirmText = '확인',
  cancelText = '취소',
  width = 300,
  height,
  disabled = false,
}: ConfirmModalProps) {
  const theme = useAppTheme();

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  return (
    <Modal onClose={onClose} width={width} height={height}>
      <div css={content}>
        <p css={title(theme)}>{confirmTitle}</p>
        {confirmMessage && <p css={message(theme)}>{confirmMessage}</p>}
      </div>
      <div css={buttonContainer}>
        <BasicButton
          variant='secondary'
          width='calc(50% - 12px)'
          onClick={onClose}
          height='30px'
        >
          {cancelText}
        </BasicButton>
        <BasicButton
          variant='primary'
          width='calc(50% - 12px)'
          onClick={handleConfirm}
          height='30px'
          disabled={disabled}
        >
          {confirmText}
        </BasicButton>
      </div>
    </Modal>
  );
}
