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

interface OnBoardingProps {
  moveNextStep: () => void;
}

export default function OnBoarding({ moveNextStep }: OnBoardingProps) {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const { groupName } = useOrganizationName();

  const handleCategoryButtonClick = (
    categoryText: '시설' | '학사행정' | '커리큘럼' | '기타'
  ) => {
    Analytics.track(onboardingEvents.categorySelect(categoryText));

    moveNextStep();
  };

  const handleViewSuggestionsClick = () => {
    Analytics.track(onboardingEvents.viewSuggestionsFromOnboarding());

    navigate('/dashboard');
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
            icon='🏠'
            text='시설'
            onClick={() => handleCategoryButtonClick('시설')}
          />
          <CategoryButton
            icon='📑'
            text='학사행정'
            onClick={() => handleCategoryButtonClick('학사행정')}
          />
          <CategoryButton
            icon='📕'
            text='커리큘럼'
            onClick={() => handleCategoryButtonClick('커리큘럼')}
          />
          <CategoryButton
            icon='💡'
            text='기타'
            onClick={() => handleCategoryButtonClick('기타')}
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
