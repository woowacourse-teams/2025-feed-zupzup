import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useState } from 'react';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: number;
}

export const useAdminModal = () => {
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

  const handleModalAction = () => {
    const { type, feedbackId } = modalState;

    if (type === 'confirm' && feedbackId) {
      console.log('피드백 완료:', feedbackId);
      patchFeedbackStatus({
        feedbackId,
        status: 'CONFIRMED',
      });
    } else if (type === 'delete' && feedbackId) {
      deleteFeedback({ feedbackId });
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
