import { useCallback, useSyncExternalStore } from 'react';
import { modalStore } from './modalStore';

export function useSyncModalActions() {
  const openModal = useCallback((content: React.ReactNode) => {
    modalStore.setState({ content, isOpen: true });
  }, []);

  const closeModal = useCallback(() => {
    modalStore.setState({ isOpen: false });
  }, []);

  return { openModal, closeModal };
}

export function useSyncModalState() {
  const { isOpen, content } = useSyncExternalStore(
    modalStore.subscribe,
    modalStore.getSnapshot
  );

  return { isOpen, content };
}
