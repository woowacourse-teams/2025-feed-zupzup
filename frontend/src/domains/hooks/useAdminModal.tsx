import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useModalContext } from '@/contexts/useModal';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useCallback, useRef } from 'react';
import AnswerModal from '../components/AnswerModal/AnswerModal';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { getLocalStorage } from '@/utils/localStorage';
import { AdminAuthData } from '@/types/adminAuth';

interface UseAdminModalProps {
  organizationId: string;
}

export const useAdminModal = ({ organizationId }: UseAdminModalProps) => {
  const queryClient = useQueryClient();
  const feedbackIdRef = useRef<number | null>(null);
  const { openModal, closeModal } = useModalContext();
  const adminName =
    getLocalStorage<AdminAuthData>('auth')?.adminName || '관리자';
  const invalidateFeedbackQueries = useCallback(() => {
    queryClient.invalidateQueries({
      queryKey: QUERY_KEYS.organizationStatistics(organizationId),
    });
    queryClient.invalidateQueries({ queryKey: QUERY_KEYS.infiniteFeedbacks });
    queryClient.invalidateQueries({
      queryKey: QUERY_KEYS.adminOrganizations(adminName),
    });
  }, [queryClient, organizationId, adminName]);

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
      const feedbackId = feedbackIdRef.current;
      if (!feedbackId) return;

      try {
        await confirmMutation.mutateAsync({ feedbackId, comment });
      } catch (e) {
        console.error(e);
        return;
      }

      closeModal();
    },
    [confirmMutation.mutateAsync, closeModal]
  );

  const handleDeleteFeedback = useCallback(async () => {
    const feedbackId = feedbackIdRef.current;
    if (!feedbackId) return;

    try {
      await deleteMutation.mutateAsync({ feedbackId });
    } catch (e) {
      console.error(e);
      return;
    }

    closeModal();
  }, [deleteMutation.mutateAsync, closeModal]);

  const openFeedbackCompleteModal = useCallback(
    (feedbackId: number) => {
      feedbackIdRef.current = feedbackId;
      openModal(
        <AnswerModal
          handleCloseModal={closeModal}
          handleSubmit={handleConfirmFeedback}
        />
      );
    },
    [openModal, closeModal, handleConfirmFeedback]
  );

  const openFeedbackDeleteModal = useCallback(
    (feedbackId: number) => {
      feedbackIdRef.current = feedbackId;
      openModal(
        <ConfirmModal
          title='삭제하시겠습니까?'
          message='삭제한 건의는 되돌릴 수 없습니다.'
          onClose={closeModal}
          onConfirm={handleDeleteFeedback}
        />
      );
    },
    [openModal, closeModal, handleDeleteFeedback]
  );

  return {
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  };
};
