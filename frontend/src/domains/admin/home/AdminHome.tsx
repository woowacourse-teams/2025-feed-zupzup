import Hero from '@/domains/user/home/components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from './components/AdminFeedbackBox';
import { useNavigate } from 'react-router-dom';
import Modal from '@/components/Modal/Modal';
import { useState } from 'react';

interface ModalState {
  type: 'confirm' | 'delete' | null;
  feedbackId?: string;
}

export default function AdminHome() {
  const navigate = useNavigate();

  const [modalState, setModalState] = useState<ModalState>({ type: null });

  const openFeedbackCompleteModal = (feedbackId: string) => {
    setModalState({ type: 'confirm', feedbackId });
  };

  const openFeedbackDeleteModal = (feedbackId: string) => {
    setModalState({ type: 'delete', feedbackId });
  };

  const closeModal = () => {
    setModalState({ type: null });
  };

  const handleModalAction = () => {
    const { type, feedbackId } = modalState;

    if (type === 'confirm') {
      console.log('피드백 완료 처리:', feedbackId);
    } else if (type === 'delete') {
      console.log('피드백 삭제 처리:', feedbackId);
    }

    closeModal();
  };

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

      {modalState.type && (
        <Modal
          title={
            modalState.type === 'delete'
              ? '삭제하시겠습니까?'
              : '확인하시겠습니까?'
          }
          message={
            modalState.type === 'delete'
              ? '삭제한 건의는 되돌릴 수 없습니다.'
              : null
          }
          isOpen={true}
          onClose={closeModal}
          type={modalState.type}
          onConfirm={handleModalAction}
        />
      )}
    </section>
  );
}
