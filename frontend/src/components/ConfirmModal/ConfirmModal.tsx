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
import { PostAdminLogoutResponse } from '@/apis/admin.api';


export interface ConfirmModalProps {
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => Promise<void | PostAdminLogoutResponse> | void;
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
  const titleId = 'confirm-modal-title';
  const confirmButtonRef = useRef<HTMLButtonElement>(null);

  const handleConfirm = async () => {
    try {
      await onConfirm?.();
      onClose();
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    confirmButtonRef.current?.focus();
  }, []);

  return (
    <Modal
      onClose={onClose}
      width={width}
      height={height}
      ariaLabelledby={titleId}
    >
      <div css={content}>
        <p id={titleId} css={title(theme)}>
          {confirmTitle}
        </p>
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
          ref={confirmButtonRef}
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
