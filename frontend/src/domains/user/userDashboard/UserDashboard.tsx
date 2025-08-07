import { useMemo } from 'react';
import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';
import useMyFeedbacks from '@/domains/user/userDashboard/hooks/useMyFeedbacks';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
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
import FilterSection from '@/domains/components/FilterSection/FilterSection';

export default function UserDashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const navigate = useNavigate();
  const theme = useAppTheme();

  // 필터/정렬 상태만 관리하는 훅
  const {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    getFilteredFeedbacks,
  } = useFeedbackFilterSort();

  const apiUrl = useMemo(() => {
    const baseUrl = '/organizations/1/feedbacks';
    const params = new URLSearchParams();

    params.append('orderBy', selectedSort);

    if (selectedFilter === 'COMPLETED') {
      params.append('status', 'CONFIRMED');
    } else if (selectedFilter === 'PENDING') {
      params.append('status', 'WAITING');
    }

    const queryString = params.toString();
    console.log('Generated API URL:', `${baseUrl}?${queryString}`); // 디버깅용

    return `${baseUrl}?${queryString}`;
  }, [selectedFilter, selectedSort]);

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
    url: apiUrl,
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

  // MINE 필터링만 클라이언트에서 처리
  const filteredAndSortedFeedbacks = getFilteredFeedbacks(feedbacks);

  return (
    <div css={dashboardLayout}>
      <DashboardOverview />
      <FilterSection
        selectedFilter={selectedFilter}
        onFilterChange={handleFilterChange}
        selectedSort={selectedSort}
        onSortChange={handleSortChange}
      />
      <div>
        <FeedbackBoxList>
          {filteredAndSortedFeedbacks.map((feedback) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status}
              content={feedback.content}
              postedAt={feedback.postedAt}
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
          feedbackCount={filteredAndSortedFeedbacks.length}
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
