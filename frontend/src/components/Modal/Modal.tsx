// components/Modal/Modal.tsx
import { SerializedStyles } from '@emotion/react';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useModal } from '@/hooks/useModal';
import BasicButton from '@/components/BasicButton/BasicButton';
import {
  overlay,
  modal,
  content,
  titleText,
  messageText,
  buttonContainer,
} from './Modal.styles';

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  type: 'delete' | 'confirm';
  title: string;
  message?: string | null;
  onConfirm?: () => void;
  width?: number;
  height?: number;
  css?: SerializedStyles;
}

export default function Modal({
  isOpen,
  onClose,
  type,
  title,
  message,
  onConfirm,
  width = 300,
  height,
  css: customCss,
}: ModalProps) {
  const theme = useAppTheme();

  const { handleOverlayClick, handleConfirm } = useModal({
    isOpen,
    onClose,
    onConfirm,
  });

  if (!isOpen) return null;

  return (
    <div css={[overlay, customCss]} onClick={handleOverlayClick}>
      <div css={modal(theme, width, height)}>
        <div css={content}>
          <p css={titleText(theme)}>{title}</p>
          {message && <p css={messageText(theme)}>{message}</p>}
        </div>
        <div css={buttonContainer}>
          {type === 'delete' ? (
            <>
              <BasicButton
                variant='secondary'
                width='calc(50% - 12px)'
                onClick={onClose}
              >
                취소
              </BasicButton>
              <BasicButton
                variant='primary'
                width='calc(50% - 12px)'
                onClick={handleConfirm}
              >
                삭제
              </BasicButton>
            </>
          ) : (
            <BasicButton variant='primary' width='100%' onClick={handleConfirm}>
              확인
            </BasicButton>
          )}
        </div>
      </div>
    </div>
  );
}
