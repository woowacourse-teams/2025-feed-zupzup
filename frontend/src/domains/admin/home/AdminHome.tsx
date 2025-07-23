import Hero from '@/domains/user/home/components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from './components/AdminFeedbackBox';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import { useAdminModal } from '@/domains/hooks/useAdminModal';

export default function AdminHome() {
  const navigate = useNavigate();
  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleModalAction,
  } = useAdminModal();

  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/')}
        onSuggestClick={() => navigate('/suggestion')}
        title='환영합니다!'
        showSuggestButton={false}
      />
      <FeedbackBoxList>
        <AdminFeedbackBox
          type='incomplete'
          feedbackId='1'
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
        />
        <AdminFeedbackBox
          type='incomplete'
          feedbackId='2'
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
        />
        <AdminFeedbackBox
          type='incomplete'
          feedbackId='3'
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
        />
        <AdminFeedbackBox
          type='complete'
          feedbackId='4'
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
        />
        <AdminFeedbackBox
          type='complete'
          feedbackId='5'
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
        />
      </FeedbackBoxList>

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
