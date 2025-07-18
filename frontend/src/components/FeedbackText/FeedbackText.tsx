import { useAppTheme } from '@/hooks/useAppTheme';
import { feedbackText } from './FeedbackText.styles';

export interface FeedbackTextProps {
  text: string;
}

export default function FeedbackText({ text }: FeedbackTextProps) {
  const theme = useAppTheme();

  return <p css={feedbackText(theme)}>{text}</p>;
}
