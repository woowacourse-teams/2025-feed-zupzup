import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { dashboardLayout } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import AdminFeedbackBox from '@/domains/admin/adminDashboard/components/AdminFeedbackBox/AdminFeedbackBox';
import useFeedbackManagement from '@/domains/admin/adminDashboard/hooks/useFeedbackManagement';
import useGetFeedback from '@/domains/admin/adminDashboard/hooks/useGetFeedback';
import DashboardOverview from '@/domains/components/DashboardOverview/DashboardOverview';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import { useAdminModal } from '@/domains/hooks/useAdminModal';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import {
  FeedbackResponse,
  FeedbackType,
  FeedbackFilterType,
} from '@/types/feedback.types';
import { useAdminAuth } from '@/hooks/useAdminAuth';
import FeedbackStatusMessage from '@/domains/user/userDashboard/components/FeedbackStatusMessage/FeedbackStatusMessage';
import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import FilterSection from '@/domains/components/FilterSection/FilterSection';
import useFeedbackFilterSort from '@/domains/hooks/useFeedbackFilterSort';
import { useMemo } from 'react';

export default function AdminDashboard() {
  const { isAuthorized, isCheckingAuth } = useAdminAuth();

  const {
    selectedFilter,
    selectedSort,
    handleFilterChange,
    handleSortChange,
    myFeedbacks,
  } = useFeedbackFilterSort();

  const apiUrl = useMemo(() => {
    const baseUrl = '/admin/organizations/1/feedbacks';
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
    items: originalFeedbacks,
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

  const {
    feedbacks: managedFeedbacks,
    optimisticConfirmFeedback,
    optimisticDeleteFeedback,
  } = useFeedbackManagement({
    originalFeedbacks,
  });

  const feedbacks = selectedFilter === 'MINE' ? myFeedbacks : managedFeedbacks;

  const {
    modalState,
    openFeedbackCompleteModal,
    openFeedbackDeleteModal,
    closeModal,
    handleConfirmFeedback,
    handleDeleteFeedback,
  } = useAdminModal({
    onConfirmFeedback: optimisticConfirmFeedback,
    onDeleteFeedback: optimisticDeleteFeedback,
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  if (isCheckingAuth) {
    return <div>로딩중...</div>;
  }

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
        {/* {loading && <div>로딩중...</div>} */}
      </FeedbackBoxList>
      <FeedbackStatusMessage
        loading={loading}
        filterType={selectedFilter as FeedbackFilterType}
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
