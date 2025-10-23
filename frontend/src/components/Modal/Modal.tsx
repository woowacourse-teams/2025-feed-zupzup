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
  ariaLabelledby?: string;
}

export default function Modal({
  onClose,
  children,
  width = 300,
  height,
  customCSS,
  disableUserClose = false,
  ariaLabelledby,
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
        role='dialog'
        aria-labelledby={ariaLabelledby}
        aria-modal='true'
        tabIndex={-1}
        css={[modalBox(theme, width, height), customCSS]}
      >
        {children}
      </div>
    </div>
  );
}
