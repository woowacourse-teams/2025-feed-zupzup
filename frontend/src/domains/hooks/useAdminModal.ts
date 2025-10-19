import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useCallback, useState } from 'react';

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
    onSuccess: invalidateFeedbackQueries,
  });

  const deleteMutation = useMutation({
    mutationFn: ({ feedbackId }: { feedbackId: number }) =>
      deleteFeedback({ feedbackId }),
    onSuccess: invalidateFeedbackQueries,
  });

  const handleConfirmFeedback = useCallback(
    async (comment: string) => {
      const { feedbackId } = modalState;
      if (!feedbackId) return;

      try {
        await confirmMutation.mutateAsync({ feedbackId, comment });
      } catch (e) {
        console.error(e);
        return;
      }

      closeModal();
    },
    [modalState.feedbackId, confirmMutation.mutateAsync, closeModal]
  );

  const handleDeleteFeedback = useCallback(async () => {
    const { feedbackId } = modalState;
    if (!feedbackId) return;

    try {
      await deleteMutation.mutateAsync({ feedbackId });
    } catch (e) {
      console.error(e);
      return;
    }

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
