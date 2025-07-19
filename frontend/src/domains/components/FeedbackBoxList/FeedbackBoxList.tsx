import { container } from './FeedbackBoxList.styles';

interface FeedbackBoxListProps {
  children: React.ReactNode;
}

export default function FeedbackBoxList({ children }: FeedbackBoxListProps) {
  return <section css={container}>{children}</section>;
}
