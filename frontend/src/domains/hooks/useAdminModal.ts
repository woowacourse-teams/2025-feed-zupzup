import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants/queryKeys';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: number;
}

interface UseAdminModalProps {
  organizationId?: number;
}

export const useAdminModal = ({ organizationId = 1 }: UseAdminModalProps) => {
  const queryClient = useQueryClient();
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const { showErrorModal } = useErrorModalContext();

  const openFeedbackCompleteModal = (feedbackId: number) =>
    setModalState({ type: 'confirm', feedbackId });

  const openFeedbackDeleteModal = (feedbackId: number) =>
    setModalState({ type: 'delete', feedbackId });

  const closeModal = () => setModalState({ type: null });

  const invalidateFeedbackQueries = () => {
    queryClient.invalidateQueries({
      queryKey: QUERY_KEYS.organizationStatistics(organizationId),
    });
    queryClient.invalidateQueries({ queryKey: QUERY_KEYS.infiniteFeedbacks });
  };

  const confirmMutation = useMutation({
    mutationFn: ({
      feedbackId,
      comment,
    }: {
      feedbackId: number;
      comment: string;
    }) => patchFeedbackStatus({ feedbackId, comment }),
    onError: () => {
      showErrorModal(
        '피드백 완료 상태 변경에 실패했습니다. 다시 시도해 주세요',
        '에러'
      );
    },
    onSuccess: invalidateFeedbackQueries,
  });

  const deleteMutation = useMutation({
    mutationFn: ({ feedbackId }: { feedbackId: number }) =>
      deleteFeedback({ feedbackId }),
    onError: () => {
      showErrorModal('피드백 삭제에 실패했습니다. 다시 시도해 주세요', '에러');
    },
    onSuccess: invalidateFeedbackQueries,
  });

  const handleConfirmFeedback = async (comment: string) => {
    const { feedbackId } = modalState;
    if (!feedbackId) return;

    await confirmMutation.mutateAsync({ feedbackId, comment });
    closeModal();
  };

  const handleDeleteFeedback = async () => {
    const { feedbackId } = modalState;
    if (!feedbackId) return;

    await deleteMutation.mutateAsync({ feedbackId });
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
