import { useState } from 'react';
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
import FilterSection from '@/components/FilterSection/FilterSection';

export default function UserDashboard() {
  const { filter, handlePanelClick } = useFeedbackFilter();
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const navigate = useNavigate();
  const theme = useAppTheme();

  const [selectedFilter, setSelectedFilter] = useState<string | null>(null);
  const [selectedSort, setSelectedSort] = useState('latest');

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

  const handleFilterChange = (newFilter: string | null) => {
    setSelectedFilter(newFilter);
  };

  const handleSortChange = (newSort: string) => {
    setSelectedSort(newSort);
  };

  const filteredAndSortedFeedbacks = (() => {
    let filtered = [...feedbacks];

    if (selectedFilter) {
      switch (selectedFilter) {
        case 'pending':
          filtered = filtered.filter(
            (feedback) => feedback.status === 'WAITING'
          );
          break;
        case 'completed':
          filtered = filtered.filter(
            (feedback) => feedback.status === 'CONFIRMED'
          );
          break;
        case 'mine':
          filtered = filtered.filter((feedback) => feedback.status === 'MINE');
          break;
      }
    }

    switch (selectedSort) {
      case 'oldest':
        filtered.sort(
          (a, b) =>
            new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
        );
        break;
      case 'likes':
        filtered.sort((a, b) => b.likeCount - a.likeCount);
        break;
      case 'latest':
      default:
        filtered.sort(
          (a, b) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        break;
    }

    return filtered;
  })();

  return (
    <div css={dashboardLayout}>
      <DashboardOverview filter={filter} handlePanelClick={handlePanelClick} />
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
