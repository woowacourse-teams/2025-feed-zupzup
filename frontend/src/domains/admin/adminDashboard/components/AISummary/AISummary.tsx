import Modal from '@/components/Modal/Modal';
import CloseIcon from '@/components/icons/CloseIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  aiSummaryContainer,
  aiSummaryTitle,
  aiSummaryDescription,
  categoriesContainer,
  categoryItem,
  categoryText,
  arrowIcon,
  footerText,
  closeButton,
  modalWidth,
} from './AISummary.styles';

interface AISummaryProps {
  isOpen: boolean;
  onClose: () => void;
  onCategorySelect?: (category: string) => void;
}

// 더미 데이터 - 실제로는 API에서 가져올 데이터
const mockCategories = [
  '사용 편의성을 개선해야 한다는 의견이 많습니다.',
  '고객 지원 응답 속도가 더 빨라지길 원합니다.',
  '가격 대비 성능이 만족스럽다는 평가가 많습니다.',
  '디자인과 UI가 직관적이지 않다는 지적이 있습니다.',
  '추가 기능 도입에 대한 요구가 많습니다.',
];

export default function AISummary({
  isOpen,
  onClose,
  onCategorySelect,
}: AISummaryProps) {
  const theme = useAppTheme();

  const handleCategoryClick = (category: string) => {
    if (onCategorySelect) {
      onCategorySelect(category);
    }
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} customCSS={modalWidth}>
      <button css={closeButton(theme)} onClick={onClose}>
        <CloseIcon />
      </button>
      <section css={aiSummaryContainer}>
        <h2 css={aiSummaryTitle(theme)}>AI 요약</h2>
        <div css={aiSummaryDescription(theme)}>
          <p>최근 100개의 피드백을 분석해</p>
          <p>연관성이 높은 항목들을 묶어 Top 5 카테고리를 보여줍니다</p>
        </div>

        <div css={categoriesContainer(theme)}>
          {mockCategories.map((category, index) => (
            <div
              key={index}
              css={categoryItem(theme)}
              onClick={() => handleCategoryClick(category)}
            >
              <p css={categoryText(theme)}>{category}</p>
              <p css={arrowIcon(theme)}>&gt;&gt;</p>
            </div>
          ))}
        </div>

        <p css={footerText(theme)}>
          카테고리를 선택하면 연관된 피드백을 보여줍니다
        </p>
      </section>
    </Modal>
  );
}
