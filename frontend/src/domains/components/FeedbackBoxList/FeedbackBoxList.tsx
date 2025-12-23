import { SerializedStyles } from '@emotion/react';
import { container } from './FeedbackBoxList.styles';

interface FeedbackBoxListProps {
  children: React.ReactNode;
  customCSS?: SerializedStyles;
}

export default function FeedbackBoxList({
  customCSS,
  children,
}: FeedbackBoxListProps) {
  return <section css={[container, customCSS]}>{children}</section>;
}
