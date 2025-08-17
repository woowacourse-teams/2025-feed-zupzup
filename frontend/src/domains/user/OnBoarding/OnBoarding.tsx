import BasicButton from '@/components/BasicButton/BasicButton';
import CategoryButton from '@/domains/components/CategoryButton/CategoryButton';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import {
  container,
  place,
  question,
  questionContainer,
  questionTitle,
  skipText,
  buttonContainer,
  title,
  skipIcon,
} from '@/domains/user/OnBoarding/OnBoarding.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useNavigate } from 'react-router-dom';
import { Analytics, onboardingEvents } from '@/analytics';
import { CategoryType } from '@/analytics/types';
import { useOrganizationId } from '@/contexts/useOrganizationId';

interface OnBoardingProps {
  onCategoryClick: (newCategory: CategoryType) => void;
}

export default function OnBoarding({ onCategoryClick }: OnBoardingProps) {
  const theme = useAppTheme();
  const navigate = useNavigate();
  const { organizationId } = useOrganizationId();

  const { groupName } = useOrganizationName({ organizationId });

  const handleViewSuggestionsClick = () => {
    Analytics.track(onboardingEvents.viewSuggestionsFromOnboarding());

    navigate(`/${organizationId}/dashboard`);
  };

  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>
          <span css={place(theme)}>{groupName}</span>에<br /> 오신 것을
          환영합니다
        </p>
        <div css={questionContainer(theme)}>
          <p css={questionTitle(theme)}>카테고리 선택</p>
          <p css={question(theme)}>건의하고 싶은 카테고리를 선택해주세요</p>
        </div>
        <div css={buttonContainer}>
          <CategoryButton
            icon='🚨'
            text='신고'
            onClick={() => onCategoryClick('신고')}
          />
          <CategoryButton
            icon='🙋‍♀️'
            text='질문'
            onClick={() => onCategoryClick('질문')}
          />
          <CategoryButton
            icon='💬'
            text='건의'
            onClick={() => onCategoryClick('건의')}
          />
          <CategoryButton
            icon='💡'
            text='기타'
            onClick={() => onCategoryClick('기타')}
          />
        </div>
      </div>
      <BasicButton
        icon={<p css={skipIcon}>📄</p>}
        variant='secondary'
        onClick={handleViewSuggestionsClick}
      >
        <p css={skipText(theme)}>건의 목록 보러가기</p>
      </BasicButton>
    </section>
  );
}
