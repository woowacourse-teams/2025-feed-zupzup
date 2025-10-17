import { ToastData } from '@/contexts/useToast';

type Listener = () => void;

interface ToastState {
  toastData: ToastData | null;
}

let state: ToastState = {
  toastData: null,
};

const listeners = new Set<Listener>();

export const toastStore = {
  subscribe(listener: Listener) {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
  getSnapshot() {
    return state;
  },
  setState(partial: Partial<ToastState>) {
    state = { ...state, ...partial };
    listeners.forEach((l) => l());
  },
};
