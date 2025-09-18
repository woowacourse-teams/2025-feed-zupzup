import { Analytics, userDashboardEvents } from '@/analytics';
import { ArrowIcon } from '@/components/icons/arrowIcon';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import useNavigation from '@/domains/hooks/useNavigation';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import {
  dashboardLayout,
  goOnboardButton,
  goTopButton,
} from '@/domains/user/userDashboard/UserDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import UserFeedbackList from './components/UserFeedbackList/UserFeedbackList';

export default function UserDashboard() {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { goPath } = useNavigation();

  const { selectedFilter, selectedSort, handleFilterChange, handleSortChange } =
    useFeedbackFilterSort();

  const { showButton, scrollToTop } = useScrollUp();

  const handleNavigateToOnboarding = () => {
    Analytics.track(userDashboardEvents.viewSuggestionsFromDashboard());
    goPath(`/${organizationId}/submit`);
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
      <UserFeedbackList
        selectedFilter={selectedFilter}
        selectedSort={selectedSort}
      />
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
    </div>
  );
}
