import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';

import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import {
  dashboardLayout,
  goOnboardButton,
  goTopButton,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { highlightStyle } from '@/domains/user/userDashboard/UserHome-delete.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';

export default function UserDashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const navigate = useNavigate();
  const theme = useAppTheme();

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
  const { showButton, scrollToTop } = useScrollUp();

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
      </div>
      <FloatingButton
        icon={<ArrowIcon />}
        onClick={() => {
          navigate('/');
        }}
        inset={['0', '0', '32px', '100%']}
        customCSS={goOnboardButton(theme)}
      />
      {showButton && (
        <FloatingButton
          icon={<ArrowUpIcon />}
          onClick={scrollToTop}
          inset={['0', '0', '32px', '0']}
          customCSS={goTopButton(theme)}
        />
      )}

      {hasNext && <div id='scroll-observer'></div>}
    </div>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}
