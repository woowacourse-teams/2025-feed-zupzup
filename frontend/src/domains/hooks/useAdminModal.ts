import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useState } from 'react';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: number;
}

interface UseAdminModalProps {
  onConfirmFeedback?: (feedbackId: number) => void;
  onDeleteFeedback?: (feedbackId: number) => void;
}

export const useAdminModal = ({
  onConfirmFeedback,
  onDeleteFeedback,
}: UseAdminModalProps = {}) => {
  const [modalState, setModalState] = useState<ModalState>({ type: null });

  const openFeedbackCompleteModal = (feedbackId: number) => {
    setModalState({ type: 'confirm', feedbackId });
  };

  const openFeedbackDeleteModal = (feedbackId: number) => {
    setModalState({ type: 'delete', feedbackId });
  };

  const closeModal = () => {
    setModalState({ type: null });
  };

  const handleModalAction = async () => {
    const { type, feedbackId } = modalState;

    if (!feedbackId) return;

    try {
      if (type === 'confirm') {
        await patchFeedbackStatus({
          feedbackId,
          status: 'CONFIRMED',
        });

        onConfirmFeedback?.(feedbackId);
      } else if (type === 'delete') {
        await deleteFeedback({ feedbackId });
        onDeleteFeedback?.(feedbackId);
      }
    } catch (error) {
      console.error('Modal action failed:', error);
    }

    closeModal();
  };

  return {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleModalAction,
  };
};
