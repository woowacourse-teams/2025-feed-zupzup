import { PropsWithChildren } from 'react';
import { container } from './FeedbackBoxList.styles';

export default function FeedbackBoxList({ children }: PropsWithChildren) {
  return <section css={container}>{children}</section>;
}
