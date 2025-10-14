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
} from './AISummaryModal.styles';
import { AISummaryCategory } from '@/types/ai.types';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useNavigation from '@/domains/hooks/useNavigation';

interface AISummaryProps {
  isOpen: boolean;
  onClose: () => void;
  onCategorySelect: (category: AISummaryCategory) => void;
  categories: AISummaryCategory[];
}

export default function AISummary({
  isOpen,
  onClose,
  onCategorySelect,
  categories,
}: AISummaryProps) {
  const { organizationId } = useOrganizationId();
  const { goPath } = useNavigation();
  const theme = useAppTheme();

  const handleCategoryClick = (category: AISummaryCategory) => {
    onCategorySelect(category);

    goPath(`/admin/${organizationId}/ai/summary/${category.clusteredId}`, {
      categoryData: category,
    });
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} customCSS={modalWidth}>
      <button css={closeButton(theme)} onClick={onClose}>
        <CloseIcon />
      </button>
      <section css={aiSummaryContainer}>
        <p css={aiSummaryTitle(theme)}>AI 요약</p>
        <div css={aiSummaryDescription(theme)}>
          <p>최근 100개의 피드백을 분석해</p>
          <p>연관성이 높은 항목들을 묶어 Top 5 카테고리를 보여줍니다</p>
        </div>

        <div css={categoriesContainer(theme)}>
          {categories.map((category, index) => (
            <div
              key={index}
              css={categoryItem(theme)}
              onClick={() => handleCategoryClick(category)}
            >
              <p css={categoryText(theme)}>
                {category.content} ({category.totalCount})
              </p>
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
