import { ApiError } from '@/apis/apiClient';
import {
  getMyFeedbacks,
  GetMyFeedbacksResponse,
} from '@/apis/userFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { FeedbackType, SortType } from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { useQuery } from '@tanstack/react-query';
import { useEffect, useMemo } from 'react';
import {} from 'react-router-dom';

export function useMyFeedbackData(selectedSort: SortType) {
  const { organizationId } = useOrganizationId();

  const feedbackIds = useMemo(
    () => [...new Set(getLocalStorage<number[]>('myFeedbacks') || [])],
    []
  );

  const { data: myFeedbacks, refetch: refetchMyFeedbacks } = useQuery<
    GetMyFeedbacksResponse,
    ApiError,
    FeedbackType[]
  >({
    queryKey: QUERY_KEYS.myFeedbacks(
      organizationId!,
      feedbackIds,
      selectedSort
    ),

    queryFn: () =>
      getMyFeedbacks({
        organizationId,
        feedbackIds,
        orderBy: selectedSort,
      }),
    enabled: organizationId !== undefined && feedbackIds.length > 0,
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
  });

  useEffect(() => {
    refetchMyFeedbacks();
  }, [selectedSort, feedbackIds]);

  return { myFeedbacks: myFeedbacks || [] };
}
