import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useFeedbackFilter from '@/domains/user/userDashboard/hooks/useFeedbackFilter';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import {
  dashboardLayout,
  goOnboardButton,
  goTopButton,
  highlightStyle,
  myFeedbackStyle,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { FeedbackResponse, FeedbackType } from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';
import FeedbackStatusMessage from './components/FeedbackStatusMessage/FeedbackStatusMessage';
import { Analytics, userDashboardEvents } from '@/analytics';

export default function UserDashboard() {
  const { filter, handlePanelClick } = useFeedbackFilter();
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
  const { getIsMyFeedback } = useMyFeedbacks();
  const { showButton, scrollToTop } = useScrollUp();

  const handleNavigateToOnboarding = () => {
    Analytics.track(userDashboardEvents.viewSuggestionsFromDashboard());

    navigate('/');
  };

  return (
    <div css={dashboardLayout}>
      <DashboardOverview filter={filter} handlePanelClick={handlePanelClick} />
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
              isMyFeedback={getIsMyFeedback(feedback.feedbackId)}
              customCSS={[
                myFeedbackStyle(theme, getIsMyFeedback(feedback.feedbackId)),
                feedback.feedbackId === highlightedId ? highlightStyle : null,
              ]}
            />
          ))}
          {loading && <div>로딩중...</div>}
        </FeedbackBoxList>
        <FeedbackStatusMessage
          loading={loading}
          hasNext={hasNext}
          feedbackCount={feedbacks.length}
        />
      </div>
      <FloatingButton
        icon={<ArrowIcon />}
        onClick={handleNavigateToOnboarding}
        inset={{ bottom: '32px', left: '100%' }}
        customCSS={goOnboardButton(theme)}
      />
      {showButton && (
        <FloatingButton
          icon={<ArrowUpIcon />}
          onClick={scrollToTop}
          inset={{ bottom: '32px' }}
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
