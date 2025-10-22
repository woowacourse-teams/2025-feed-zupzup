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
    </FeedbackBoxList>
  );
}
