import { useAdminAuth } from '@/hooks/useAdminAuth';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import useFeedbackManagement from '@/domains/admin/adminDashboard/hooks/useFeedbackManagement';
import { FeedbackType } from '@/types/feedback.types';
import useDashboardData from '@/domains/hooks/useDashboardData';

export default function useAdminDashboard() {
  const { feedbacks: originalFeedbacks, ...dashboardData } = useDashboardData({
    isAdmin: true,
  });

  const { isAuthorized, isCheckingAuth } = useAdminAuth();

  const { feedbacks, confirmFeedback, deleteFeedback } = useFeedbackManagement({
    originalFeedbacks,
  });

  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleModalAction,
  } = useAdminModal({
    onConfirmFeedback: confirmFeedback,
    onDeleteFeedback: deleteFeedback,
  });

  const getAdminFeedbackProps = (feedback: FeedbackType) => ({
    key: feedback.feedbackId,
    feedbackId: feedback.feedbackId,
    onConfirm: openFeedbackCompleteModal,
    onDelete: openFeedbackDeleteModal,
    type: feedback.status,
    content: feedback.content,
    postedAt: feedback.postedAt,
    isSecret: feedback.isSecret,
    likeCount: feedback.likeCount,
    userName: feedback.userName,
  });

  return {
    ...dashboardData,
    feedbacks,

    isAuthorized,
    isCheckingAuth,
    modalState,
    closeModal,
    handleModalAction,
    getAdminFeedbackProps,
  };
}
