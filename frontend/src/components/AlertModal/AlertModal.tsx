import Modal from '@/components/Modal/Modal';
import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  content,
  title,
  message,
  buttonContainer,
} from '@/components/Modal/Modal.styles';
import { useEffect, useRef } from 'react';

export interface AlertModalProps {
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
}

export default function AlertModal({
  onClose,
  title: alertTitle,
  message: alertMessage,
  onConfirm,
  confirmText = '확인',
}: AlertModalProps) {
  const theme = useAppTheme();
  const titleId = 'alert-modal-title';
  const confirmButtonRef = useRef<HTMLButtonElement>(null);

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  useEffect(() => {
    confirmButtonRef.current?.focus();
  }, []);

  return (
    <Modal onClose={onClose} ariaLabelledby={titleId}>
      <div css={content}>
        <p id={titleId} css={title(theme)}>
          {alertTitle}
        </p>
        {alertMessage && <p css={message(theme)}>{alertMessage}</p>}
      </div>
      <div css={buttonContainer}>
        <BasicButton
          ref={confirmButtonRef}
          variant='primary'
          width='100%'
          height='30px'
          onClick={handleConfirm}
        >
          {confirmText}
        </BasicButton>
      </div>
    </Modal>
  );
}
