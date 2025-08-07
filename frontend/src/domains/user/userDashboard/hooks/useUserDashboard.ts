import { useNavigate } from 'react-router-dom';
import { Analytics, userDashboardEvents } from '@/analytics';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import { useAppTheme } from '@/hooks/useAppTheme';
import { getLocalStorage } from '@/utils/localStorage';
import {
  highlightStyle,
  myFeedbackStyle,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { FeedbackType } from '@/types/feedback.types';
import useDashboardData from '@/domains/hooks/useDashboardData';

export default function useUserDashboard() {
  const navigate = useNavigate();
  const theme = useAppTheme();
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];

  // 공통 데이터
  const dashboardData = useDashboardData({ isAdmin: false });

  // 유저 전용 상태들
  const { highlightedId } = useHighLighted();
  const { getIsMyFeedback } = useMyFeedbacks();
  const { showButton, scrollToTop } = useScrollUp();

  // 핸들러
  const handleNavigateToOnboarding = () => {
    Analytics.track(userDashboardEvents.viewSuggestionsFromDashboard());
    navigate('/');
  };

  const getFeedbackIsLike = (feedbackId: number) => {
    return likedFeedbackIds?.includes(feedbackId) || false;
  };

  const getUserFeedbackProps = (feedback: FeedbackType) => ({
    userName: feedback.userName,
    key: feedback.feedbackId,
    type: feedback.status,
    content: feedback.content,
    postedAt: feedback.postedAt,
    isLiked: getFeedbackIsLike(feedback.feedbackId),
    isSecret: feedback.isSecret,
    feedbackId: feedback.feedbackId,
    likeCount: feedback.likeCount,
    isMyFeedback: getIsMyFeedback(feedback.feedbackId),
    customCSS: [
      myFeedbackStyle(theme, getIsMyFeedback(feedback.feedbackId)),
      feedback.feedbackId === highlightedId ? highlightStyle : null,
    ],
  });

  return {
    ...dashboardData,

    theme,
    showScrollButton: showButton,
    handleNavigateToOnboarding,
    scrollToTop,
    getUserFeedbackProps,
  };
}
