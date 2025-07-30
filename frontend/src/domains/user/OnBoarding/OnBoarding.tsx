import { getOrganizationName } from '@/apis/organization.api';
import BasicButton from '@/components/BasicButton/BasicButton';
import SkipIcon from '@/components/icons/SkipIcon';
import {
  container,
  place,
  question,
  questionContainer,
  questionTitle,
  skipText,
  buttonContainer,
  title,
} from '@/domains/user/OnBoarding/OnBoarding.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useEffect, useState } from 'react';

interface OnBoardingProps {
  moveNextStep: () => void;
}

export default function OnBoarding({ moveNextStep }: OnBoardingProps) {
  const theme = useAppTheme();

  const [placeName, setPlaceName] = useState('피드줍줍');

  useEffect(() => {
    async function getOrganization() {
      const response = await getOrganizationName({ organizationId: 1 });
      setPlaceName(response!.data.organizationName);
    }
    getOrganization();
  }, []);

  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>
          <span css={place(theme)}>{placeName}</span>에<br /> 오신 것을
          환영합니다
        </p>
        <div css={questionContainer(theme)}>
          <p css={questionTitle(theme)}>오늘의 질문</p>
          <p css={question(theme)}>"오늘 캠퍼스 분위기 어떠세요?"</p>
        </div>
        <div css={buttonContainer}>
          <BasicButton icon='😊' variant='secondary' onClick={moveNextStep}>
            <p>좋아요</p>
          </BasicButton>
          <BasicButton icon='😐' variant='secondary' onClick={moveNextStep}>
            <p>보통이에요</p>
          </BasicButton>
          <BasicButton icon='😔' variant='secondary' onClick={moveNextStep}>
            <p>별로에요</p>
          </BasicButton>
        </div>
      </div>
      <BasicButton icon={<SkipIcon />} variant='secondary'>
        <p css={skipText(theme)}>건너뛰기</p>
      </BasicButton>
    </section>
  );
}
