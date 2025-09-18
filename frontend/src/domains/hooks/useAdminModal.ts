import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useState, useCallback } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants/queryKeys';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: number;
}

interface UseAdminModalProps {
  organizationId: string;
}

export const useAdminModal = ({ organizationId }: UseAdminModalProps) => {
  const queryClient = useQueryClient();
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const { showErrorModal } = useErrorModalContext();

  const openFeedbackCompleteModal = useCallback(
    (feedbackId: number) => setModalState({ type: 'confirm', feedbackId }),
    []
  );

  const openFeedbackDeleteModal = useCallback(
    (feedbackId: number) => setModalState({ type: 'delete', feedbackId }),
    []
  );

  const closeModal = useCallback(() => setModalState({ type: null }), []);

  const invalidateFeedbackQueries = useCallback(() => {
    queryClient.invalidateQueries({
      queryKey: QUERY_KEYS.organizationStatistics(organizationId),
    });
    queryClient.invalidateQueries({ queryKey: QUERY_KEYS.infiniteFeedbacks });
  }, [queryClient, organizationId]);

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

  const handleConfirmFeedback = useCallback(
    async (comment: string) => {
      const { feedbackId } = modalState;
      if (!feedbackId) return;

      await confirmMutation.mutateAsync({ feedbackId, comment });
      closeModal();
    },
    [modalState.feedbackId, confirmMutation.mutateAsync, closeModal]
  );

  const handleDeleteFeedback = useCallback(async () => {
    const { feedbackId } = modalState;
    if (!feedbackId) return;

    await deleteMutation.mutateAsync({ feedbackId });
    closeModal();
  }, [modalState.feedbackId, deleteMutation.mutateAsync, closeModal]);

  return {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  };
};
