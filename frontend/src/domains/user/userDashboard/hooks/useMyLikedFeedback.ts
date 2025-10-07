import { getMyLikedFeedbacks } from '@/apis/userFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

export default function useMyLikedFeedback() {
  const { data: myLikeFeedbackIds, refetch: refetchMyLikeFeedbackIds } =
    useQuery({
      queryKey: QUERY_KEYS.myLikeFeedbackIds,
      queryFn: getMyLikedFeedbacks,
      select: (res) => res.data.feedbackIds,
    });

  return { myLikeFeedbackIds: myLikeFeedbackIds, refetchMyLikeFeedbackIds };
}
