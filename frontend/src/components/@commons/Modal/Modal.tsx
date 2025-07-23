import { SerializedStyles } from '@emotion/react';
import { useModal } from '@/hooks/useModal';

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  customCSS?: SerializedStyles | SerializedStyles[];
}

export default function Modal({
  isOpen,
  onClose,
  children,
  customCSS,
}: ModalProps) {
  const { handleOverlayClick } = useModal({
    isOpen,
    onClose,
  });

  if (!isOpen) return null;

  return (
    <div css={customCSS} onClick={handleOverlayClick}>
      {children}
    </div>
  );
}
