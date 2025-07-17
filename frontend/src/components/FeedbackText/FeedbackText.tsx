import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackTextProps } from './FeedbackText.types';
import { feedbackText } from './FeedbackText.styles';

export default function FeedbackText({ text }: FeedbackTextProps) {
  const theme = useAppTheme();

  return <p css={feedbackText(theme)}>{text}</p>;
}
