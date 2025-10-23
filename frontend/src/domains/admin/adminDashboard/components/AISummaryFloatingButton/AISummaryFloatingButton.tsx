import { useAppTheme } from '@/hooks/useAppTheme';
import { useModalContext } from '@/contexts/useModal';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import AISummaryModal from '../AISummaryModal/AISummaryModal';
import { aiFloatingButton } from '@/domains/admin/adminDashboard/AdminDashboard.style';

export default function AISummaryFloatingButton() {
  const theme = useAppTheme();
  const { openModal, closeModal: closeAiModal } = useModalContext();

  const handleCategoryClick = () => {
    openModal(<AISummaryModal onClose={closeAiModal} />);
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
