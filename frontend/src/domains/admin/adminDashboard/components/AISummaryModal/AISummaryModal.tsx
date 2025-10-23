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
import useAISummary from './useAISummary';
import { useParams } from 'react-router-dom';
import StatusBox from '@/domains/components/StatusBox/StatusBox';

interface AISummaryProps {
  onClose: () => void;
}

export default function AISummary({ onClose }: AISummaryProps) {
  const { organizationId } = useOrganizationId();
  const { goPath } = useNavigation();
  const theme = useAppTheme();
  const { data, isLoading } = useAISummary({ organizationId });
  const { clusterId } = useParams();

  const handleCategoryClick = (category: AISummaryCategory) => {
    if (clusterId === category.clusterId.toString()) {
      onClose();
      return;
    }

    const isInSummary = location.pathname.includes('/ai/summary/');

    goPath(`/admin/${organizationId}/ai/summary/${category.clusterId}`, {
      replace: isInSummary,
    });

    onClose();
  };

  return (
    <Modal onClose={onClose} customCSS={modalWidth}>
      <button css={closeButton(theme)} onClick={onClose}>
        <CloseIcon />
      </button>
      <section css={aiSummaryContainer}>
        <p css={aiSummaryTitle(theme)}>AI 요약</p>
        <div css={aiSummaryDescription(theme)}>
          <p>최근 100개의 피드백을 분석해</p>
          <p>연관성이 높은 항목들을 묶어 Top 5 카테고리를 보여줍니다</p>
        </div>

        {isLoading ? (
          <div>loading...</div>
        ) : (
          <div css={categoriesContainer(theme)}>
            {data?.clusterInfos.length === 0 ? (
              <StatusBox
                width={'100%'}
                height={'200px'}
                textIcon='💭'
                title='아직 데이터를 모으는 중이에요.'
                description='피드백이 더 작성되면 AI 요약을 볼 수 있어요!'
              />
            ) : (
              data?.clusterInfos.map((category, index) => (
                <div
                  key={index}
                  css={categoryItem(theme)}
                  onClick={() => handleCategoryClick(category)}
                >
                  <p css={categoryText(theme)}>
                    {category.label} ({category.totalCount})
                  </p>
                  <p css={arrowIcon(theme)}>&gt;&gt;</p>
                </div>
              ))
            )}
          </div>
        )}

        <p css={footerText(theme)}>
          카테고리를 선택하면 연관된 피드백을 보여줍니다
        </p>
      </section>
    </Modal>
  );
}
