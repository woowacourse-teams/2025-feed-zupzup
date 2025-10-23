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
import { srFeedbackSummary } from '@/domains/user/userDashboard/utils/srFeedbackSummary';
import { formatRelativeTime } from '@/utils/formatRelativeTime';
import { skipTarget } from '@/domains/user/userDashboard/UserDashboard.style';

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
      <div id='user-feedback-list' tabIndex={-1} css={skipTarget}>
        <FeedbackBoxList>
          {displayFeedbacks.map((feedback: FeedbackType) => {
            const isMyFeedback = myFeedbacks.some(
              (myFeedback) => myFeedback.feedbackId === feedback.feedbackId
            );
            const postedAt = formatRelativeTime(feedback.postedAt ?? '');
            return (
              <div key={feedback.feedbackId}>
                <span className='srOnly'>
                  {srFeedbackSummary({
                    feedback,
                    myFeedback: isMyFeedback,
                    postedAt,
                    isAdmin: false,
                  })}
                </span>
                <UserFeedbackBox
                  userName={feedback.userName}
                  type={feedback.status}
                  content={feedback.content}
                  postedAt={postedAt}
                  isLiked={getFeedbackIsLike(feedback.feedbackId) || false}
                  isSecret={feedback.isSecret}
                  feedbackId={feedback.feedbackId}
                  likeCount={feedback.likeCount}
                  comment={feedback.comment}
                  isMyFeedback={isMyFeedback}
                  isHighlighted={feedback.feedbackId === highlightedId}
                  category={feedback.category}
                  imgUrl={feedback.imageUrl}
                />
              </div>
            );
          })}
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
