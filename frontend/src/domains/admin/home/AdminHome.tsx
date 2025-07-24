import Hero from '@/domains/user/home/components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from './components/AdminFeedbackBox';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import { useAdminModal } from '@/domains/hooks/useAdminModal';

import useInfinityScroll from '@/hooks/useInfinityScroll';
import { AdminFeedback, FeedbackResponse } from '@/types/feedback.types';
import useGetFeedback from '@/domains/admin/home/hooks/useGetFeedback';

export default function AdminHome() {
  const navigate = useNavigate();

  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleModalAction,
  } = useAdminModal();

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    AdminFeedback,
    'feedbacks',
    FeedbackResponse<AdminFeedback>
  >({
    url: '/api/admin/places/1/feedbacks',
    key: 'feedbacks',
  });

  useGetFeedback({ fetchMore, hasNext });

  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/')}
        onSuggestClick={() => navigate('/suggestion')}
        title='우테코'
        isUserPage={false}
      />
      <FeedbackBoxList>
        {feedbacks.map((feedback: AdminFeedback) => (
          <AdminFeedbackBox
            key={feedback.feedbackId}
            feedbackId={feedback.feedbackId}
            onConfirm={openFeedbackCompleteModal}
            onDelete={openFeedbackDeleteModal}
            type={feedback.status}
            content={feedback.content}
            createdAt={feedback.createdAt}
            isSecret={feedback.isSecret}
            imgUrl={feedback.imgUrl}
            likeCount={feedback.likeCount}
            userName={feedback.userName}
          />
        ))}
        {loading && <div>로딩중...</div>}
      </FeedbackBoxList>
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
