import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { feedbackData } from '@/mocks/data/feedbackData';
import AdminFeedbackBox from '../adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import { aiSummaryTitle } from './AISummary.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function AISummary() {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  } = useAdminModal({ organizationId });

  return (
    <FeedbackBoxList>
      <p css={aiSummaryTitle(theme)}>
        사용 편의성을 개선해야 한다는 의견이 많습니다 (5)
      </p>
      {feedbackData.map((feedback) => (
        <AdminFeedbackBox
          key={feedback.feedbackId}
          feedbackId={feedback.feedbackId}
          onConfirm={openFeedbackCompleteModal}
          onDelete={openFeedbackDeleteModal}
          type={feedback.status}
          content={feedback.content}
          postedAt={feedback.postedAt}
          isSecret={feedback.isSecret}
          likeCount={feedback.likeCount}
          userName={feedback.userName}
          category={feedback.category}
          comment={feedback.comment}
        />
      ))}
      {modalState.type === 'delete' && (
        <ConfirmModal
          title='삭제하시겠습니까?'
          message='삭제한 건의는 되돌릴 수 없습니다.'
          isOpen={true}
          onClose={closeModal}
          onConfirm={handleDeleteFeedback}
        />
      )}
      {modalState.type === 'confirm' && (
        <AnswerModal
          isOpen={true}
          handleCloseModal={closeModal}
          handleSubmit={handleConfirmFeedback}
        />
      )}
    </FeedbackBoxList>
  );
}
