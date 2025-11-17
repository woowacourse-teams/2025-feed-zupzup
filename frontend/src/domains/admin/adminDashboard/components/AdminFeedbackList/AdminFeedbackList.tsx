import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FeedbackBoxSkeletonList from '@/domains/components/FeedbackBoxSkeleton/FeedbackBoxSkeletonList';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';
import { skipTarget } from '@/domains/user/userDashboard/UserDashboard.style';
import { srFeedbackSummary } from '@/domains/user/userDashboard/utils/srFeedbackSummary';
import { createFeedbacksUrl } from '@/domains/utils/createFeedbacksUrl';
import useCursorInfiniteScroll from '@/hooks/useCursorInfiniteScroll';
import {
  FeedbackFilterType,
  FeedbackResponse,
  FeedbackType,
  SortType,
} from '@/types/feedback.types';
import { formatRelativeTime } from '@/utils/formatRelativeTime';
import { memo, useMemo } from 'react';
import useGetFeedback from '../../hooks/useGetFeedback';
import AdminFeedbackBox from '../AdminFeedbackBox/AdminFeedbackBox';

interface AdminFeedbackListProps {
  selectedFilter: '' | FeedbackFilterType;
  selectedSort: SortType;
  openFeedbackCompleteModal: (feedbackId: number) => void;
  openFeedbackDeleteModal: (feedbackId: number) => void;
}

export default memo(function AdminFeedbackList({
  selectedFilter,
  selectedSort,
  openFeedbackCompleteModal,
  openFeedbackDeleteModal,
}: AdminFeedbackListProps) {
  const { organizationId } = useOrganizationId();
  const apiUrl = useMemo(
    () =>
      createFeedbacksUrl({
        organizationId,
        sort: selectedSort,
        filter: selectedFilter,
        isAdmin: true,
      }),
    [organizationId, selectedSort, selectedFilter]
  );

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
    <div id='admin-feedback-list' tabIndex={-1} css={skipTarget}>
      <FeedbackBoxList>
        {feedbacks.map((feedback) => {
          const postedAt = formatRelativeTime(feedback.postedAt ?? '');
          return (
            <div key={feedback.feedbackId}>
              <span className='srOnly'>
                {srFeedbackSummary({
                  feedback,
                  myFeedback: false,
                  postedAt,
                  isAdmin: true,
                })}
              </span>
              <AdminFeedbackBox
                feedbackId={feedback.feedbackId}
                onConfirm={openFeedbackCompleteModal}
                onDelete={openFeedbackDeleteModal}
                type={feedback.status}
                content={feedback.content}
                postedAt={postedAt}
                isSecret={feedback.isSecret}
                likeCount={feedback.likeCount}
                userName={feedback.userName}
                category={feedback.category}
                comment={feedback.comment}
                imgUrl={feedback.imageUrl}
              />
            </div>
          );
        })}
      </FeedbackBoxList>
      {loading && <FeedbackBoxSkeletonList count={2} />}
      <div>
        {!loading && (
          <FeedbackStatusMessage
            loading={loading}
            filterType={selectedFilter as FeedbackFilterType}
            hasNext={hasNext}
            feedbackCount={feedbacks.length}
          />
        )}
        {hasNext && <div id='scroll-observer' style={{ minHeight: '1px' }} />}
      </div>
    </div>
  );
});
