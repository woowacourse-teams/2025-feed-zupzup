type Listener = () => void;

interface ModalState {
  content: React.ReactNode;
  isOpen: boolean;
}

let state: ModalState = {
  content: null,
  isOpen: false,
};

const listeners = new Set<Listener>();

export const modalStore = {
  subscribe(listener: Listener) {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
  getSnapshot() {
    return state;
  },
  setState(partial: Partial<ModalState>) {
    state = { ...state, ...partial };
    listeners.forEach((l) => l());
  },
};
