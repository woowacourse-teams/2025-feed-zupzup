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

    if (type === 'confirm') {
      console.log('피드백 완료 처리:', feedbackId);
      // TODO: 실제 피드백 완료 API 호출
    } else if (type === 'delete') {
      console.log('피드백 삭제 처리:', feedbackId);
      // TODO: 실제 피드백 삭제 API 호출
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
