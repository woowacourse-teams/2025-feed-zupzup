import { createPortal } from 'react-dom';
import { useSyncModalState } from './useModal';

export default function ModalProvider() {
  const { isOpen, content } = useSyncModalState();
  const portalTarget = document.getElementById('modal') || document.body;

  return <>{isOpen && createPortal(<>{content}</>, portalTarget)}</>;
}
