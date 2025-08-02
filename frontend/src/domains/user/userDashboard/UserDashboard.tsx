import { ArrowIcon } from '@/components/icons/arrowIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import { dashboardLayout } from '@/domains/user/userDashboard/UserDashboard.style';
import { highlightStyle } from '@/domains/user/userDashboard/UserHome-delete.styles';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';
import StatusBox from '@/domains/components/StatusBox/StatusBox';

export default function UserDashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const navigate = useNavigate();

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    FeedbackType,
    'feedbacks',
    FeedbackResponse<FeedbackType>
  >({
    url: '/organizations/1/feedbacks',
    key: 'feedbacks',
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  const { highlightedId } = useHighLighted();

  return (
    <div css={dashboardLayout}>
      <DashboardOverview />
      <div>
        <FeedbackBoxList>
          {feedbacks.map((feedback) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status}
              content={feedback.content}
              createdAt={feedback.createdAt}
              isLiked={getFeedbackIsLike(likedFeedbackIds, feedback.feedbackId)}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
              customCSS={
                feedback.feedbackId === highlightedId ? highlightStyle : null
              }
            />
          ))}
          {loading && <div>로딩중...</div>}
        </FeedbackBoxList>
        {!hasNext && !loading && (
          <StatusBox
            width={'100%'}
            height={200}
            textIcon='🎉'
            title='모든 피드백을 다 보셨어요!'
            description='현재 보실 수 있는 피드백은 여기까지예요.'
          />
        )}
      </div>
      <FloatingButton
        icon={<ArrowIcon />}
        onClick={() => {
          navigate('/');
        }}
      />
      {hasNext && <div id='scroll-observer'></div>}
    </div>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}
