import { SerializedStyles } from '@emotion/react';
import { useModal } from '@/hooks/useModal';
import { useAppTheme } from '@/hooks/useAppTheme';
import { overlay, modalBox } from './Modal.styles';

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  width?: number;
  height?: number | undefined;
  customCSS?: SerializedStyles | SerializedStyles[];
  preventClose?: boolean;
}

export default function Modal({
  isOpen,
  onClose,
  children,
  width = 300,
  height,
  customCSS,
  preventClose = false,
}: ModalProps) {
  const theme = useAppTheme();
  const { handleOverlayClick } = useModal({
    isOpen,
    onClose,
    preventClose,
  });

  if (!isOpen) return null;

  return (
    <div css={overlay} onClick={handleOverlayClick}>
      <div css={[modalBox(theme, width, height), customCSS]}>{children}</div>
    </div>
  );
}
