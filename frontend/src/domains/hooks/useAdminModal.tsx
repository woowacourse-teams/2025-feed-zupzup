import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useCallback, useRef } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants/queryKeys';
import AnswerModal from '../components/AnswerModal/AnswerModal';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { useModalActions } from '@/stores/useModal';

interface UseAdminModalProps {
  organizationId: string;
}

export const useAdminModal = ({ organizationId }: UseAdminModalProps) => {
  const queryClient = useQueryClient();
  const feedbackIdRef = useRef<number | null>(null);
  const { openModal, closeModal } = useModalActions();
  const { showErrorModal } = useErrorModalContext();

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
      const feedbackId = feedbackIdRef.current;
      if (!feedbackId) return;

      await confirmMutation.mutateAsync({ feedbackId, comment });
      closeModal();
    },
    [confirmMutation.mutateAsync, closeModal]
  );

  const handleDeleteFeedback = useCallback(async () => {
    const feedbackId = feedbackIdRef.current;
    if (!feedbackId) return;

    await deleteMutation.mutateAsync({ feedbackId });
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
