import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useAppTheme } from '@/hooks/useAppTheme';
import useGoTop from './useGoTop';
import { useParams } from 'react-router-dom';
import useAIDetail from './useAIDetail';
import { aiSummaryTitle } from './AISummary.styles';
import AdminFeedbackBox from '../adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import NotFoundPage from '@/components/NotFoundPage/NotFoundPage';
import AISummaryFloatingButton from '../adminDashboard/components/AISummaryFloatingButton/AISummaryFloatingButton';
import StatusBox from '@/domains/components/StatusBox/StatusBox';

export default function AISummary() {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { clusterId } = useParams();
  const { openFeedbackCompleteModal, openFeedbackDeleteModal } = useAdminModal({
    organizationId,
  });
  useGoTop();

  if (!clusterId || isNaN(Number(clusterId))) {
    return <NotFoundPage />;
  }

  const { data } = useAIDetail({
    organizationId,
    clusterId: Number(clusterId),
  });

  if (data?.feedbacks.length === 0) {
    return (
      <StatusBox
        width={'100%'}
        height={'200px'}
        textIcon='💭'
        title='아직 데이터를 모으는 중이에요.'
        description='피드백이 더 작성되면 AI 요약을 볼 수 있어요!'
      />
    );
  }

  return (
    <FeedbackBoxList>
      <p css={aiSummaryTitle(theme)}>
        {data?.label} ({data?.totalCount})
      </p>
      {data?.feedbacks.map((feedback) => (
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
          imgUrl={feedback.imageUrl}
        />
      ))}
      <AISummaryFloatingButton />
    </FeedbackBoxList>
  );
}
