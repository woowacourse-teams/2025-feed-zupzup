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
import { memo, useMemo } from 'react';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import { aiFloatingButton } from '../../AdminDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';

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
  const theme = useAppTheme();
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
      // 여기 범위는 어떻게 해야할 지 정하고 수정 예정
      {feedbacks.length > 10 && (
        <FloatingButton
          text='AI'
          onClick={() => {}}
          inset={{ bottom: '60px', left: '100%' }}
          customCSS={aiFloatingButton(theme)}
        />
      )}
    </div>
  );
});
