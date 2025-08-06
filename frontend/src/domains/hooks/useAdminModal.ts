import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useState } from 'react';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: number;
}

interface UseAdminModalProps {
  onConfirmFeedback?: (feedbackId: number, comment: string) => void;
  onDeleteFeedback?: (feedbackId: number) => void;
}

export const useAdminModal = ({
  onConfirmFeedback,
  onDeleteFeedback,
}: UseAdminModalProps = {}) => {
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const { showErrorModal } = useErrorModalContext();

  const openFeedbackCompleteModal = (feedbackId: number) => {
    setModalState({ type: 'confirm', feedbackId });
  };

  const openFeedbackDeleteModal = (feedbackId: number) => {
    setModalState({ type: 'delete', feedbackId });
  };

  const closeModal = () => {
    setModalState({ type: null });
  };

  const handleConfirmFeedback = async (comment: string) => {
    const { feedbackId } = modalState;
    if (feedbackId) {
      onConfirmFeedback?.(feedbackId, comment);
      try {
        await patchFeedbackStatus({
          feedbackId,
          comment,
        });
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }
    closeModal();
  };

  const handleDeleteFeedback = async () => {
    const { feedbackId } = modalState;
    if (feedbackId) {
      onDeleteFeedback?.(feedbackId);
      try {
        await deleteFeedback({ feedbackId });
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }
    closeModal();
  };

  return {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  };
};
