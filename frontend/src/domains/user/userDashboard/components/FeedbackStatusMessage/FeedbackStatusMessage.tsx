import StatusBox from '@/domains/components/StatusBox/StatusBox';

interface FeedbackStatusMessageProps {
  loading: boolean;
  hasNext: boolean;
  feedbackCount: number;
}

export default function FeedbackStatusMessage({
  loading,
  hasNext,
  feedbackCount,
}: FeedbackStatusMessageProps) {
  if (loading) return null;

  if (feedbackCount === 0) {
    return (
      <StatusBox
        width={'100%'}
        height={'200px'}
        textIcon='💭'
        title='아직 피드백이 없어요'
        description='첫 번째 피드백을 작성해보세요!'
      />
    );
  }

  if (!hasNext) {
    return (
      <StatusBox
        width={'100%'}
        height={'200px'}
        textIcon='🎉'
        title='모든 피드백을 다 보셨어요!'
        description='현재 보실 수 있는 피드백은 여기까지예요.'
      />
    );
  }

  return null;
}
