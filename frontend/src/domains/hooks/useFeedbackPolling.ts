import { getOrganizationStatistics } from '@/apis/organization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useToast } from '@/contexts/useToast';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { StatisticsProps } from '@/domains/hooks/useUserOrganizationsStatistics';
import { GetOrganizationStatistics } from '@/types/organization.types';
import { useQuery } from '@tanstack/react-query';
import { useEffect, useMemo } from 'react';

interface FeedbackPollingProps {
  statistics: StatisticsProps;
}

export function useFeedbackPolling({ statistics }: FeedbackPollingProps) {
  const { organizationId } = useOrganizationId();
  const { showToast } = useToast();

  const { data: pollingData } = useQuery({
    queryKey: QUERY_KEYS.organizationPollingStatistics(organizationId),
    queryFn: () => getOrganizationStatistics({ organizationId }),
    select: (res: GetOrganizationStatistics) => res.data,
    refetchInterval: 5000,
  });

  const feedbackDiff = useMemo(() => {
    if (!pollingData?.totalCount || !statistics.totalCount) return 0;
    return Number(pollingData.totalCount) - Number(statistics.totalCount);
  }, [pollingData?.totalCount, statistics?.totalCount]);

  useEffect(() => {
    if (statistics.totalCount === undefined) return;
    if (feedbackDiff > 0) {
      showToast(
        `새로운 피드백 ${feedbackDiff}건이 도착했습니다! 새로고침 해주세요.`,
        'origin',
        5000
      );
    }
  }, [feedbackDiff, showToast]);

  return { feedbackDiff };
}
