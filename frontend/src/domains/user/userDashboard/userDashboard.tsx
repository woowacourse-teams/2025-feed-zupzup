import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import { FEEDBACK_MOCK } from '@/domains/mocks/feedback.mock';
import {
  dashboardLayout,
  titleText,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { getLocalStorage } from '@/utils/localStorage';

const GROUP_NAME = '우아한테크코스';

export default function UserDashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const theme = useAppTheme();

  // const {
  //   items: originalFeedbacks,
  //   fetchMore,
  //   hasNext,
  //   loading,
  // } = useInfinityScroll<
  //   AdminFeedback,
  //   'feedbacks',
  //   FeedbackResponse<AdminFeedback>
  // >({
  //   url: '/admin/places/1/feedbacks',
  //   key: 'feedbacks',
  // });

  // const { feedbacks, confirmFeedback, deleteFeedback } = useFeedbackManagement({
  //   originalFeedbacks,
  // });

  // const {
  //   modalState,
  //   openFeedbackCompleteModal,
  //   openFeedbackDeleteModal,
  //   closeModal,
  //   handleModalAction,
  // } = useAdminModal({
  //   onConfirmFeedback: confirmFeedback,
  //   onDeleteFeedback: deleteFeedback,
  // });

  // useGetFeedback({ fetchMore, hasNext, loading });

  return (
    <div css={dashboardLayout}>
      <p css={titleText(theme)}>{GROUP_NAME}</p>
      <DashboardOverview />
      <div>
        <FeedbackBoxList>
          {FEEDBACK_MOCK.map((feedback) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status as FeedbackStatusType}
              content={feedback.content}
              createdAt={feedback.createdAt}
              isLiked={getFeedbackIsLike(likedFeedbackIds, feedback.feedbackId)}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
            />
          ))}
          {/* {loading && <div>로딩중...</div>} */}
        </FeedbackBoxList>
      </div>
    </div>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}
