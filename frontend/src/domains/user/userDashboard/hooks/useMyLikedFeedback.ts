import { getMyLikedFeedbacks } from '@/apis/userFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export default function useMyLikedFeedback() {
  const { organizationId } = useOrganizationId();
  const { data: myLikeFeedbackIds, refetch: refetchMyLikeFeedbackIds } =
    useQuery({
      queryKey: QUERY_KEYS.myLikeFeedbackIds(organizationId),
      queryFn: () => getMyLikedFeedbacks({ organizationId }),
      select: (res) => res.data.feedbackIds,
    });

  return { myLikeFeedbackIds, refetchMyLikeFeedbackIds };
}
