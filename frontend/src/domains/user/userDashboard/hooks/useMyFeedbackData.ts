import { ApiError } from '@/apis/apiClient';
import {
  getMyFeedbacks,
  GetMyFeedbacksResponse,
} from '@/apis/userFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { FeedbackType } from '@/types/feedback.types';

import { useQuery } from '@tanstack/react-query';

export function useMyFeedbackData() {
  const { organizationId } = useOrganizationId();

  const { data: myFeedbacks } = useQuery<
    GetMyFeedbacksResponse,
    ApiError,
    FeedbackType[]
  >({
    queryKey: QUERY_KEYS.myFeedbacks(organizationId),
    select: (res) => res.data.feedbacks,
    queryFn: () =>
      getMyFeedbacks({
        organizationId,
      }),

    enabled: organizationId !== '',
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
  });

  return { myFeedbacks: myFeedbacks || [] };
}
