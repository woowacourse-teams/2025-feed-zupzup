import { useAppTheme } from '@/hooks/useAppTheme';
import { feedbackText } from './FeedbackText.styles';

export interface FeedbackTextProps {
  text: string;
  type: 'incomplete' | 'complete';
}

export default function FeedbackText({ text, type }: FeedbackTextProps) {
  const theme = useAppTheme();

  return <p css={feedbackText(theme, type)}>{text}</p>;
}
