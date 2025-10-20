import { SerializedStyles } from '@emotion/react';
import { useModal } from '@/hooks/useModal';
import { useAppTheme } from '@/hooks/useAppTheme';
import { overlay, modalBox } from './Modal.styles';
import { useEffect, useRef } from 'react';

export interface ModalProps {
  onClose: () => void;
  children: React.ReactNode;
  width?: number;
  height?: number | undefined;
  customCSS?: SerializedStyles | SerializedStyles[];
  disableUserClose?: boolean;
}

export default function Modal({
  onClose,
  children,
  width = 300,
  height,
  customCSS,
  disableUserClose = false,
}: ModalProps) {
  const theme = useAppTheme();
  const modalRef = useRef<HTMLDivElement>(null);
  const { handleOverlayClick } = useModal({
    onClose,
    disableUserClose,
  });

  useEffect(() => {
    if (modalRef.current) {
      modalRef.current.focus();
    }
  }, []);

  return (
    <div css={overlay} onClick={handleOverlayClick}>
      <div
        ref={modalRef}
        css={[modalBox(theme, width, height), customCSS]}
        role='dialog'
        aria-modal='true'
        tabIndex={-1}
      >
        {children}
      </div>
    </div>
  );
}
