import AlertModal from '@/components/AlertModal/AlertModal';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import AdminFeedbackBox from '@/domains/admin/adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import useFeedbackManagement from '@/domains/admin/adminDashboard/hooks/useFeedbackManagement';
import { FEEDBACK_MOCK } from '@/domains/admin/adminDashboard/mocks/adminFeedback.mock';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export default function AdminDashboard() {
  // const {
  //   items: originalFeedbacks,
  //   fetchMore,
  //   hasNext,
  //   loading,
  // } = useInfinityScroll<
  //   AdminFeedback,
  //   'feedbacks',
  //   FeedbackResponse<AdminFeedback>
  // >({
  //   url: '/admin/places/1/feedbacks',
  //   key: 'feedbacks',
  // });

  const { feedbacks, confirmFeedback, deleteFeedback } = useFeedbackManagement({
    originalFeedbacks: FEEDBACK_MOCK,
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

  // useGetFeedback({ fetchMore, hasNext, loading });

  return (
    <section>
      <FeedbackBoxList>
        {feedbacks.map((feedback) => (
          <AdminFeedbackBox
            key={feedback.feedbackId}
            feedbackId={feedback.feedbackId}
            onConfirm={openFeedbackCompleteModal}
            onDelete={openFeedbackDeleteModal}
            type={feedback.status as FeedbackStatusType}
            content={feedback.content}
            createdAt={feedback.createdAt}
            isSecret={feedback.isSecret}
            likeCount={feedback.likeCount}
            userName={feedback.userName}
          />
        ))}
        {/* {loading && <div>로딩중...</div>} */}
      </FeedbackBoxList>
      {/* {hasNext && <div id='scroll-observer'></div>} */}

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
