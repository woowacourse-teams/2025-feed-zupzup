import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from '../AdminFeedbackBox/AdminFeedbackBox';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';
import {
  FeedbackFilterType,
  FeedbackResponse,
  FeedbackType,
  SortType,
} from '@/types/feedback.types';
import useCursorInfiniteScroll from '@/hooks/useCursorInfiniteScroll';
import useGetFeedback from '../../hooks/useGetFeedback';
import { createFeedbacksUrl } from '@/domains/utils/createFeedbacksUrl';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';

interface AdminFeedbackListProps {
  selectedFilter: '' | FeedbackFilterType;
  selectedSort: SortType;
  openFeedbackCompleteModal: (feedbackId: number) => void;
  openFeedbackDeleteModal: (feedbackId: number) => void;
}

export default function AdminFeedbackList({
  selectedFilter,
  selectedSort,
  openFeedbackCompleteModal,
  openFeedbackDeleteModal,
}: AdminFeedbackListProps) {
  const { organizationId } = useOrganizationId();

  const apiUrl = createFeedbacksUrl({
    organizationId,
    sort: selectedSort,
    filter: selectedFilter,
    isAdmin: true,
  });

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useCursorInfiniteScroll<
    FeedbackType,
    'feedbacks',
    FeedbackResponse<FeedbackType>
  >({
    url: apiUrl,
    key: 'feedbacks',
    size: 10,
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  return (
    <div>
      <FeedbackBoxList>
        {feedbacks.map((feedback) => (
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
      </FeedbackBoxList>
      <div>
        <FeedbackStatusMessage
          loading={loading}
          filterType={selectedFilter as FeedbackFilterType}
          hasNext={hasNext}
          feedbackCount={feedbacks.length}
        />

        {hasNext && <div id='scroll-observer' style={{ minHeight: '1px' }} />}
      </div>
    </div>
  );
}
