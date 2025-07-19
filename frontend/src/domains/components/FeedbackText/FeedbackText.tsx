import { useAppTheme } from '@/hooks/useAppTheme';
import { feedbackText } from './FeedbackText.styles';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface FeedbackTextProps {
  text: string;
  type: FeedbackStatusType;
}

export default function FeedbackText({ text, type }: FeedbackTextProps) {
  const theme = useAppTheme();

  return <p css={feedbackText(theme, type)}>{text}</p>;
}
