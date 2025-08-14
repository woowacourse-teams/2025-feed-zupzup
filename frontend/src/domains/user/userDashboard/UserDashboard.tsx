import { Analytics, userDashboardEvents } from '@/analytics';
import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import useHighLighted from '@/domains/user/userDashboard/hooks/useHighLighted';

import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import {
  dashboardLayout,
  goOnboardButton,
  goTopButton,
  highlightStyle,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import {
  FeedbackResponse,
  FeedbackType,
  FeedbackFilterType,
} from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';
import FeedbackStatusMessage from './components/FeedbackStatusMessage/FeedbackStatusMessage';
import { useMemo } from 'react';

export default function UserDashboard() {
  const organizationId = 1;
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const navigate = useNavigate();
  const theme = useAppTheme();

  const {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    myFeedbacks,
  } = useFeedbackFilterSort();

  const apiUrl = useMemo(() => {
    const baseUrl = `/organizations/${organizationId}/feedbacks`;
    const params = new URLSearchParams();

    params.append('orderBy', selectedSort);

    if (selectedFilter === 'CONFIRMED') {
      params.append('status', 'CONFIRMED');
    } else if (selectedFilter === 'WAITING') {
      params.append('status', 'WAITING');
    }

    return `${baseUrl}?${params.toString()}`;
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

  const filteredAndSortedFeedbacks = useMemo(() => {
    if (selectedFilter === 'MINE') {
      return myFeedbacks;
    }
    return feedbacks;
  }, [selectedFilter, myFeedbacks, feedbacks]);

  const { highlightedId } = useHighLighted();
  const { showButton, scrollToTop } = useScrollUp();

  const handleNavigateToOnboarding = () => {
    Analytics.track(userDashboardEvents.viewSuggestionsFromDashboard());
    navigate('/');
  };

  const getFeedbackIsLike = (feedbackId: number) => {
    return likedFeedbackIds?.includes(feedbackId) || false;
  };

  return (
    <div css={dashboardLayout}>
      <DashboardOverview />
      <FilterSection
        selectedFilter={selectedFilter}
        onFilterChange={handleFilterChange}
        selectedSort={selectedSort}
        onSortChange={handleSortChange}
        isAdmin={false}
      />
      <div>
        <FeedbackBoxList>
          {filteredAndSortedFeedbacks.map((feedback: FeedbackType) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status}
              content={feedback.content}
              postedAt={feedback.postedAt}
              isLiked={getFeedbackIsLike(feedback.feedbackId) || false}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
              comment={feedback.comment}
              isMyFeedback={myFeedbacks.some(
                (myFeedback) => myFeedback.feedbackId === feedback.feedbackId
              )}
              customCSS={[
                feedback.feedbackId === highlightedId ? highlightStyle : null,
              ]}
              category={feedback.category}
            />
          ))}
          {/* {loading && <div>로딩중...</div>} */}
        </FeedbackBoxList>
        <FeedbackStatusMessage
          loading={loading}
          filterType={selectedFilter as FeedbackFilterType}
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
