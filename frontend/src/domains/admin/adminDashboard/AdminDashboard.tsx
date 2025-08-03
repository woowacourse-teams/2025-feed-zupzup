import AlertModal from '@/components/AlertModal/AlertModal';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { dashboardLayout } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import AdminFeedbackBox from '@/domains/admin/adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import useFeedbackManagement from '@/domains/admin/adminDashboard/hooks/useFeedbackManagement';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';
import { useAdminAuth } from '@/hooks/useAdminAuth';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';

export default function AdminDashboard() {
  const {
    items: originalFeedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    FeedbackType,
    'feedbacks',
    FeedbackResponse<FeedbackType>
  >({
    url: '/admin/organizations/1/feedbacks',
    key: 'feedbacks',
  });

  const { feedbacks, confirmFeedback, deleteFeedback } = useFeedbackManagement({
    originalFeedbacks,
  });

  const { isAuthorized, isCheckingAuth } = useAdminAuth();

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

  useGetFeedback({ fetchMore, hasNext, loading });

  if (isCheckingAuth) {
    return <div>로딩중...</div>;
  }

  if (!isAuthorized) {
    return null;
  }

  return (
    <section css={dashboardLayout}>
      <DashboardOverview filter='전체' handlePanelClick={() => {}} />
      <FeedbackBoxList>
        {feedbacks.map((feedback) => (
          <AdminFeedbackBox
            key={feedback.feedbackId}
            feedbackId={feedback.feedbackId}
            onConfirm={openFeedbackCompleteModal}
            onDelete={openFeedbackDeleteModal}
            type={feedback.status}
            content={feedback.content}
            createdAt={feedback.createdAt}
            isSecret={feedback.isSecret}
            likeCount={feedback.likeCount}
            userName={feedback.userName}
          />
        ))}
        {loading && <div>로딩중...</div>}
      </FeedbackBoxList>
      <FeedbackStatusMessage
        loading={loading}
        hasNext={hasNext}
        feedbackCount={feedbacks.length}
      />
      {hasNext && <div id='scroll-observer'></div>}

      {modalState.type === 'delete' && (
        <ConfirmModal
          title='삭제하시겠습니까?'
          message='삭제한 건의는 되돌릴 수 없습니다.'
          isOpen={true}
          onClose={closeModal}
          onConfirm={handleModalAction}
        />
      )}
      {modalState.type === 'confirm' && (
        <AlertModal
          title='확인하시겠습니까?'
          isOpen={true}
          onClose={closeModal}
          onConfirm={handleModalAction}
        />
      )}
    </section>
  );
}
