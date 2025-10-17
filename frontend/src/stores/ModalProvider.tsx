import { createPortal } from 'react-dom';
import { useModalState } from './useModal';

export default function ModalProvider() {
  const { isOpen, content } = useModalState();
  const portalTarget = document.getElementById('modal') || document.body;

  return <>{isOpen && createPortal(<>{content}</>, portalTarget)}</>;
}
