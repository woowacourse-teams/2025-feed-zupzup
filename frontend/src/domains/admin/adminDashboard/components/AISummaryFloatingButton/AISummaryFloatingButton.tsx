import { useAppTheme } from '@/hooks/useAppTheme';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import AISummaryModal from '../AISummaryModal/AISummaryModal';
import { aiFloatingButton } from '@/domains/admin/adminDashboard/AdminDashboard.style';
import { useModalActions } from '@/stores/Modal/useModal';

export default function AISummaryFloatingButton() {
  const theme = useAppTheme();
  const { openModal, closeModal: closeAiModal } = useModalActions();

  const dummyCategories = [
    {
      clusteredId: '1',
      content: '사용 편의성을 개선해야 한다는 의견이 많습니다.',
      totalCount: 10,
    },
    {
      clusteredId: '2',
      content: '고객 지원 응답 속도가 더 빨라지길 원합니다.',
      totalCount: 5,
    },
    {
      clusteredId: '3',
      content: '가격 대비 성능이 만족스럽다는 평가가 많습니다.',
      totalCount: 3,
    },
    {
      clusteredId: '4',
      content: '디자인과 UI가 직관적이지 않다는 지적이 있습니다.',
      totalCount: 8,
    },
    {
      clusteredId: '5',
      content: '추가 기능 도입에 대한 요구가 많습니다.',
      totalCount: 12,
    },
  ];

  const handleCategoryClick = () => {
    openModal(
      <AISummaryModal
        onCategorySelect={() => {
          '아직은 빈값 나중에 api 연결할 때 추가';
        }}
        onClose={closeAiModal}
        categories={dummyCategories}
      />
    );
  };

  return (
    <FloatingButton
      text='AI'
      onClick={handleCategoryClick}
      inset={{ bottom: '60px', left: '100%' }}
      customCSS={aiFloatingButton(theme)}
    />
  );
}
