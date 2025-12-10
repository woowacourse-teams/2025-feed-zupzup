import {
  container,
  title,
} from '@/domains/components/FeedbackAnswer/FeedbackAnswer.styles';
import { multilineTextWrap } from '@/domains/styles/text.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface FeedbackAnswerProps {
  answer: string;
}

export default function FeedbackAnswer({ answer }: FeedbackAnswerProps) {
  const theme = useAppTheme();
  return (
    <div css={container(theme)}>
      <p css={title(theme)}>관리자 답변</p>
      <p css={multilineTextWrap}>{answer}</p>
    </div>
  );
}
