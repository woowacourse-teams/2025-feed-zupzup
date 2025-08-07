import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { dashboardLayout } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import AdminFeedbackBox from '@/domains/admin/adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';
import useAdminDashboard from './hooks/useAdminDashboard';

export default function AdminDashboard() {
  const {
    feedbacks,
    loading,
    hasNext,
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    isAuthorized,
    isCheckingAuth,
    modalState,
    closeModal,
    handleModalAction,
    getAdminFeedbackProps,
  } = useAdminDashboard();

  if (!isAuthorized) {
    return null;
  }

  return (
    <section css={dashboardLayout}>
      <DashboardOverview />

      <FilterSection
        selectedFilter={selectedFilter}
        onFilterChange={handleFilterChange}
        selectedSort={selectedSort}
        onSortChange={handleSortChange}
        isAdmin={true}
      />

      <FeedbackBoxList>
        {feedbacks.map((feedback) => (
          <AdminFeedbackBox {...getAdminFeedbackProps(feedback)} />
        ))}
      </FeedbackBoxList>

      <FeedbackStatusMessage
        loading={loading}
        hasNext={hasNext}
        feedbackCount={feedbacks.length}
      />

      {hasNext && <div id='scroll-observer'></div>}

      {modalState.type === 'delete' && (
        <ConfirmModal
          title='삭제하시겠습니까?'
          message='삭제한 건의는 되돌릴 수 없습니다.'
          isOpen={true}
          onClose={closeModal}
          onConfirm={handleModalAction}
        />
      )}

      {modalState.type === 'confirm' && (
        <AnswerModal
          isOpen={true}
          handleCloseModal={closeModal}
          handleSubmit={handleModalAction}
        />
      )}
    </section>
  );
}
