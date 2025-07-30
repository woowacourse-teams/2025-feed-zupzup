import BasicButton from '@/components/BasicButton/BasicButton';
import SkipIcon from '@/components/icons/SkipIcon';
import CategoryButton from '@/domains/components/CategoryButton/CategoryButton';
import {
  container,
  place,
  question,
  questionContainer,
  questionTitle,
  skipText,
  buttonContainer,
  title,
} from '@/domains/components/OnBoarding/OnBoarding.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface OnBoardingProps {
  moveNextStep: () => void;
}

export default function OnBoarding({ moveNextStep }: OnBoardingProps) {
  const theme = useAppTheme();

  const handleCategoryButtonClick = () => {
    // api 통신
    moveNextStep();
  };

  const placeName = '우아한테크코스 캠퍼스';
  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>
          <span css={place(theme)}>{placeName}</span>에<br /> 오신 것을
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
            onClick={handleCategoryButtonClick}
          />
          <CategoryButton
            icon='📑'
            text='학사행정'
            onClick={handleCategoryButtonClick}
          />
          <CategoryButton
            icon='👥'
            text='인간관계'
            onClick={handleCategoryButtonClick}
          />
          <CategoryButton
            icon='💡'
            text='기타'
            onClick={handleCategoryButtonClick}
          />
        </div>
      </div>
      <BasicButton icon={<SkipIcon />} variant='secondary'>
        <p css={skipText(theme)}>건너뛰기</p>
      </BasicButton>
    </section>
  );
}
