import { SerializedStyles } from '@emotion/react';
import { useAppTheme } from '@/hooks/useAppTheme';
import BasicButton from '@/components/BasicButton/BasicButton';
import {
  overlay,
  modal,
  content,
  title,
  message,
  buttonContainer,
} from './Modal.styles';
import { useEffect } from 'react';

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  type: 'delete' | 'confirm';
  title?: string;
  message?: string;
  onConfirm?: () => void;
  width?: number;
  height?: number;
  css?: SerializedStyles;
}

export default function Modal({
  isOpen,
  onClose,
  type,
  title: customTitle,
  message: customMessage,
  onConfirm,
  width = 300,
  height,
  css: customCss,
}: ModalProps) {
  const theme = useAppTheme();

  if (!isOpen) return null;
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [onClose]);

  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };

  const handleConfirm = () => {
    onConfirm?.();
    onClose();
  };

  const defaultTitle =
    type === 'delete' ? '건의를 삭제하시겠습니까?' : '건의를 확인하시겠습니까?';
  const defaultMessage =
    type === 'delete' ? '삭제한 건의는 되돌릴 수 없습니다.' : null;

  return (
    <div css={[overlay, customCss]} onClick={handleOverlayClick}>
      <div css={modal(theme, width, height)}>
        <div css={content}>
          <p css={title(theme)}>{customTitle || defaultTitle}</p>
          {defaultMessage && (
            <p css={message(theme)}>{customMessage || defaultMessage}</p>
          )}
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
