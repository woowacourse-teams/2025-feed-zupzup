import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { container } from './FeedbackBoxBackGround.styles';
import { SerializedStyles } from '@emotion/react';

interface FeedbackBoxBackGroundProps {
  children: React.ReactNode;
  type: FeedbackStatusType;
  customCSS?: (SerializedStyles | null)[];
}

export default function FeedbackBoxBackGround({
  children,
  type,
  customCSS,
}: FeedbackBoxBackGroundProps) {
  const theme = useAppTheme();

  return (
    <section css={[container(theme, type), customCSS]}>{children}</section>
  );
}
