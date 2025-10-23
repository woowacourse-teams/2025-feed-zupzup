import {
  container,
  title,
} from '@/domains/components/FeedbackAnswer/FeedbackAnswer.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface FeedbackAnswerProps {
  answer: string;
}

export default function FeedbackAnswer({ answer }: FeedbackAnswerProps) {
  const theme = useAppTheme();
  return (
    <div css={container(theme)} aria-hidden={true}>
      <p css={title(theme)}>관리자 답변</p>
      <p>{answer}</p>
    </div>
  );
}
