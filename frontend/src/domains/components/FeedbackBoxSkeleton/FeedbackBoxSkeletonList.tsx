import FeedbackBoxSkeleton from './FeedbackBoxSkeleton';
import { container } from '@/domains/components/FeedbackBoxSkeleton/FeedbackBoxSkeletonList.styles';
interface FeedbackBoxSkeletonListProps {
  count?: number;
}

export default function FeedbackBoxSkeletonList({
  count = 3,
}: FeedbackBoxSkeletonListProps) {
  return (
    <div css={container}>
      {Array.from({ length: count }, (_, index) => (
        <FeedbackBoxSkeleton key={index} />
      ))}
    </div>
  );
}
