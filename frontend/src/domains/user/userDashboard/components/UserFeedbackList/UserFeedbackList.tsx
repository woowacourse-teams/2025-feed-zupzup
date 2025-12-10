import FeedbackBoxSkeletonList from '@/domains/components/FeedbackBoxSkeleton/FeedbackBoxSkeletonList';
import VirtualFeedbackItem from '@/domains/components/VirtualFeedbackItem/VirtualFeedbackItem';
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
import { formatRelativeTime } from '@/utils/formatRelativeTime';
import { useVirtualizer, VirtualItem } from '@tanstack/react-virtual';
import { memo, useCallback, useEffect, useMemo, useRef } from 'react';
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
    isFetchingNextPage,
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

  const myFeedbackIdSet = useMemo(
    () => new Set(myFeedbacks.map((f) => f.feedbackId)),
    [myFeedbacks]
  );

  const parentRef = useRef<HTMLDivElement>(null);

  const rowVirtualizer = useVirtualizer({
    count: hasNext ? feedbacks.length + 1 : feedbacks.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 200,
    overscan: 3,
  });

  const virtualItems = rowVirtualizer.getVirtualItems();
  const lastVirtualIndex = virtualItems[virtualItems.length - 1]?.index;

  useEffect(() => {
    if (lastVirtualIndex === undefined) return;

    if (
      lastVirtualIndex >= displayFeedbacks.length - 1 &&
      hasNext &&
      !isFetchingNextPage &&
      !loading
    ) {
      fetchMore();
    }
  }, [
    lastVirtualIndex,
    displayFeedbacks.length,
    hasNext,
    isFetchingNextPage,
    loading,
    fetchMore,
  ]);

  return (
    <div>
      <div
        id='user-feedback-list'
        tabIndex={-1}
        ref={parentRef}
        style={{
          height: 'calc(100vh - 200px)',
          overflowY: 'auto',
          contain: 'strict',
        }}
      >
        <div
          style={{
            position: 'relative',
            width: '100%',
            height: `${rowVirtualizer.getTotalSize()}px`,
          }}
        >
          {virtualItems.map((virtualRow: VirtualItem) => {
            const isLoaderRow = virtualRow.index > displayFeedbacks.length - 1;
            const feedback = displayFeedbacks[virtualRow.index];

            if (isLoaderRow) {
              return (
                <div
                  key={`loading-${virtualRow.index}`}
                  style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: virtualRow.size,
                    transform: `translateY(${virtualRow.start}px)`,
                  }}
                >
                  <div>가져오는중</div>
                </div>
              );
            }

            if (!feedback) return null;

            const isMyFeedback = myFeedbackIdSet.has(feedback.feedbackId);
            const postedAt = formatRelativeTime(feedback.postedAt ?? '');

            return (
              <VirtualFeedbackItem
                key={virtualRow.key}
                measureElement={rowVirtualizer.measureElement}
                virtualRow={virtualRow}
              >
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
              </VirtualFeedbackItem>
            );
          })}
        </div>

        {loading && displayFeedbacks.length === 0 && (
          <FeedbackBoxSkeletonList count={2} />
        )}

        {loading && <FeedbackBoxSkeletonList count={2} />}
        <FeedbackStatusMessage
          loading={loading}
          filterType={selectedFilter as FeedbackFilterType}
          hasNext={hasNext}
          feedbackCount={displayFeedbacks.length}
        />
      </div>
    </div>
  );
});
