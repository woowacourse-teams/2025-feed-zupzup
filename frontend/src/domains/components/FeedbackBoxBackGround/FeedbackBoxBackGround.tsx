import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { container } from './FeedbackBoxBackGround.styles';

interface FeedbackBoxBackGroundProps {
  children: React.ReactNode;
  type: FeedbackStatusType;
}

export default function FeedbackBoxBackGround({
  children,
  type,
}: FeedbackBoxBackGroundProps) {
  const theme = useAppTheme();

  return <section css={container(theme, type)}>{children}</section>;
}
