import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { dashboardLayout } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import AdminFeedbackBox from '@/domains/admin/adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import {
  FeedbackResponse,
  FeedbackType,
  FeedbackFilterType,
} from '@/types/feedback.types';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';
import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import useCursorInfiniteScroll from '@/hooks/useCursorInfiniteScroll';
import { createFeedbacksUrl } from '@/domains/utils/createFeedbacksUrl';

export default function AdminDashboard() {
  const { selectedFilter, selectedSort, handleFilterChange, handleSortChange } =
    useFeedbackFilterSort();

  const apiUrl = createFeedbacksUrl({
    organizationId: 1,
    sort: selectedSort,
    filter: selectedFilter,
    isAdmin: true,
  });

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useCursorInfiniteScroll<
    FeedbackType,
    'feedbacks',
    FeedbackResponse<FeedbackType>
  >({
    url: apiUrl,
    key: 'feedbacks',
    size: 10,
  });

  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  } = useAdminModal({ organizationId: 1 });

  useGetFeedback({ fetchMore, hasNext, loading });

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
          <AdminFeedbackBox
            key={feedback.feedbackId}
            feedbackId={feedback.feedbackId}
            onConfirm={openFeedbackCompleteModal}
            onDelete={openFeedbackDeleteModal}
            type={feedback.status}
            content={feedback.content}
            postedAt={feedback.postedAt}
            isSecret={feedback.isSecret}
            likeCount={feedback.likeCount}
            userName={feedback.userName}
            category={feedback.category}
            comment={feedback.comment}
          />
        ))}
      </FeedbackBoxList>

      <FeedbackStatusMessage
        loading={loading}
        filterType={selectedFilter as FeedbackFilterType}
        hasNext={hasNext}
        feedbackCount={feedbacks.length}
      />

      {hasNext && <div id='scroll-observer' style={{ minHeight: '1px' }} />}

      {modalState.type === 'delete' && (
        <ConfirmModal
          title='삭제하시겠습니까?'
          message='삭제한 건의는 되돌릴 수 없습니다.'
          isOpen={true}
          onClose={closeModal}
          onConfirm={handleDeleteFeedback}
        />
      )}

      {modalState.type === 'confirm' && (
        <AnswerModal
          isOpen={true}
          handleCloseModal={closeModal}
          handleSubmit={handleConfirmFeedback}
        />
      )}
    </section>
  );
}
