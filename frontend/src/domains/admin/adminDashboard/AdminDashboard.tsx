import { SEO } from '@/components/SEO/SEO';
import { dashboardLayout } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useScrollUp from '@/domains/user/userDashboard/hooks/useScrollUp';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import ArrowUpIcon from '@/components/icons/ArrowUpIcon';
import { goTopButton } from '@/domains/user/userDashboard/UserDashboard.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import AdminFeedbackList from './components/AdminFeedbackList/AdminFeedbackList';
import AISummaryFloatingButton from './components/AISummaryFloatingButton/AISummaryFloatingButton';

export default function AdminDashboard() {
  const theme = useAppTheme();
  const { selectedFilter, selectedSort, handleFilterChange, handleSortChange } =
    useFeedbackFilterSort();
  const { organizationId } = useOrganizationId();

  const { openFeedbackCompleteModal, openFeedbackDeleteModal } = useAdminModal({
    organizationId,
  });

  const { showButton, scrollToTop } = useScrollUp();

  return (
    <>
      <SEO
        title='피드백 관리'
        description='수집된 피드백을 관리하고 답변하세요'
        keywords='관리자, 대시보드, 피드백, 관리'
      />
      <section css={dashboardLayout}>
        <DashboardOverview />
        <FilterSection
          selectedFilter={selectedFilter}
          onFilterChange={handleFilterChange}
          selectedSort={selectedSort}
          onSortChange={handleSortChange}
          isAdmin={true}
        />
        <AdminFeedbackList
          selectedFilter={selectedFilter}
          selectedSort={selectedSort}
          openFeedbackCompleteModal={openFeedbackCompleteModal}
          openFeedbackDeleteModal={openFeedbackDeleteModal}
        />
        <AISummaryFloatingButton />
        {showButton && (
          <FloatingButton
            icon={<ArrowUpIcon />}
            onClick={scrollToTop}
            inset={{ bottom: '60px' }}
            customCSS={goTopButton(theme)}
          />
        )}
      </section>
    </>
  );
}
