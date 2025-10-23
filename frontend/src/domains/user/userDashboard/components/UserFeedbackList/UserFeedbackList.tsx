import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import useMyLikedFeedback from '@/domains/user/userDashboard/hooks/useMyLikedFeedback';
import { createFeedbacksUrl } from '@/domains/utils/createFeedbacksUrl';
import useCursorInfiniteScroll from '@/hooks/useCursorInfiniteScroll';
import {
  FeedbackFilterType,
  FeedbackResponse,
  FeedbackType,
  SortType,
} from '@/types/feedback.types';
import { memo, useCallback, useMemo } from 'react';
import { useMyFeedbackData } from '../../hooks/useMyFeedbackData';
import FeedbackStatusMessage from '../FeedbackStatusMessage/FeedbackStatusMessage';

interface UserFeedbackListProps {
  selectedFilter: '' | FeedbackFilterType;
  selectedSort: SortType;
}

export default memo(function UserFeedbackList({
  selectedFilter,
  selectedSort,
}: UserFeedbackListProps) {
  const { organizationId } = useOrganizationId();
  const { myLikeFeedbackIds } = useMyLikedFeedback();

  const apiUrl = useMemo(
    () =>
      createFeedbacksUrl({
        organizationId,
        sort: selectedSort,
        filter: selectedFilter,
        isAdmin: false,
      }),
    [organizationId, selectedSort, selectedFilter]
  );

  const shouldUseInfiniteScroll = useMemo(
    () => selectedFilter !== 'MINE',
    [selectedFilter]
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
    enabled: shouldUseInfiniteScroll,
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  const { myFeedbacks } = useMyFeedbackData();

  const { highlightedId } = useHighLighted();

  const displayFeedbacks = useMemo(
    () => (selectedFilter === 'MINE' ? myFeedbacks : feedbacks),
    [selectedFilter, myFeedbacks, feedbacks]
  );

  const getFeedbackIsLike = useCallback(
    (feedbackId: number) => {
      return myLikeFeedbackIds?.includes(feedbackId) || false;
    },
    [myLikeFeedbackIds]
  );

  return (
    <>
      <div id='user-feedback-list'>
        <FeedbackBoxList>
          {displayFeedbacks.map((feedback: FeedbackType) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status}
              content={feedback.content}
              postedAt={feedback.postedAt}
              isLiked={getFeedbackIsLike(feedback.feedbackId) || false}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
              comment={feedback.comment}
              isMyFeedback={myFeedbacks.some(
                (myFeedback) => myFeedback.feedbackId === feedback.feedbackId
              )}
              isHighlighted={feedback.feedbackId === highlightedId}
              category={feedback.category}
              imgUrl={feedback.imageUrl}
            />
          ))}
        </FeedbackBoxList>
        <FeedbackStatusMessage
          loading={loading}
          filterType={selectedFilter as FeedbackFilterType}
          hasNext={hasNext}
          feedbackCount={displayFeedbacks.length}
        />
        {hasNext && <div id='scroll-observer' style={{ minHeight: '1px' }} />}
      </div>
    </>
  );
});
