import { createContext, useCallback, useContext, useState } from 'react';
import { createPortal } from 'react-dom';

interface ErrorModalContextProps {
  openModal: (content: React.ReactNode) => void;
  closeModal: () => void;
  isOpen: boolean;
}

const ModalContext = createContext<ErrorModalContextProps>({
  openModal: () => {},
  closeModal: () => {},
  isOpen: false,
});

interface ModalProviderProps {
  children: React.ReactNode;
}
export function ModalProvider({ children }: ModalProviderProps) {
  const [content, setContent] = useState<React.ReactNode | null>(null);
  const isOpen = content != null;

  const openModal = useCallback((content: React.ReactNode) => {
    setContent(content);
  }, []);

  const closeModal = useCallback(() => {
    setContent(null);
  }, []);

  const portalTarget = document.getElementById('modal') || document.body;

  return (
    <ModalContext.Provider
      value={{
        openModal,
        closeModal,
        isOpen,
      }}
    >
      {children}
      {isOpen && createPortal(<>{content}</>, portalTarget)}
    </ModalContext.Provider>
  );
}

export function useModalContext() {
  const context = useContext(ModalContext);
  if (!context) {
    throw new Error(
      '컨텍스트는 ModalContextProvider 내부에서 사용해야 합니다.'
    );
  }
  return context;
}
