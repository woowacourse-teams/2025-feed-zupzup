import BasicButton from '@/components/BasicButton/BasicButton';
import SkipIcon from '@/components/icons/SkipIcon';
import {
  container,
  subTitle,
  question,
  questionContainer,
  questionTitle,
  skipText,
  buttonContainer,
  title,
} from '@/domains/components/FeedbackPage/FeedbackPage.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface FeedbackPageProps {
  moveNextStep: () => void;
}

export default function FeedbackPage({ moveNextStep }: FeedbackPageProps) {
  const theme = useAppTheme();

  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>소중한 의견을 들려주세요</p>
        <p css={subTitle(theme)}>
          더 나은 공간을 만들기 위해 여러분의 피드백이 필요해요
        </p>
        <br />
        <p css={questionTitle(theme)}>이런 의견들이 도움돼요</p>
        <div css={questionContainer(theme)}>
          <p css={question(theme)}>
            📊 와이파이 속도가 빨라서 온라인 강의 듣기 좋아요
          </p>
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
