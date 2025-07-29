import {
  container,
  subTitle,
  question,
  questionContainer,
  questionTitle,
  title,
} from '@/domains/components/FeedbackPage/FeedbackPage.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import FeedbackInput from '@/domains/user/home/components/FeedbackInput/FeedbackInput';

export default function FeedbackPage() {
  const theme = useAppTheme();

  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>소중한 의견을 들려주세요</p>
        <p css={subTitle(theme)}>
          더 나은 공간을 만들기 위해 여러분의 <br />
          피드백이 필요해요
        </p>
        <br />
        <p css={questionTitle(theme)}>이런 의견들이 도움돼요</p>
        <div css={questionContainer(theme)}>
          <p css={question(theme)}>
            📊 와이파이 속도가 빨라서 온라인 강의 듣기 좋아요
          </p>
        </div>
        <FeedbackInput />
      </div>
    </section>
  );
}
