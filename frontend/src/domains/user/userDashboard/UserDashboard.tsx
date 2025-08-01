import { ArrowIcon } from '@/components/icons/arrowIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import { dashboardLayout } from '@/domains/user/userDashboard/UserDashboard.style';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';

import { getLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';

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

  // const storageHighlightedId = localStorage.getItem('highlightedId');

  // const [highlightedId, setHighLightedId] = useState<number | null>(null);

  // useEffect(() => {
  //   if (!storageHighlightedId) return;

  //   setHighLightedId(Number(storageHighlightedId));

  //   const timeout = setTimeout(() => {
  //     setHighLightedId(null);
  //     localStorage.removeItem('highlightedId');
  //   }, 2000);

  //   return () => clearTimeout(timeout);
  // }, []);

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
            />
          ))}
          {/* {loading && <div>로딩중...</div>} */}
        </FeedbackBoxList>
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
