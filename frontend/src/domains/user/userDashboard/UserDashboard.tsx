import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import FeedbackStatusMessage from './components/FeedbackStatusMessage/FeedbackStatusMessage';
import useUserDashboard from './hooks/useUserDashboard';
import {
  dashboardLayout,
  goOnboardButton,
  goTopButton,
} from '@/domains/user/userDashboard/UserDashboard.style';

export default function UserDashboard() {
  const {
    feedbacks,
    loading,
    hasNext,
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    theme,
    showScrollButton,
    handleNavigateToOnboarding,
    scrollToTop,
    getUserFeedbackProps,
  } = useUserDashboard();

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
          {feedbacks.map((feedback) => (
            <UserFeedbackBox {...getUserFeedbackProps(feedback)} />
          ))}
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

      {showScrollButton && (
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
