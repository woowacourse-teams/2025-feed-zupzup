import { QUERY_KEYS } from '@/constants/queryKeys';
import { useToast } from '@/contexts/useToast';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useUserOrganizationsStatistics, {
  StatisticsProps,
} from '@/domains/hooks/useUserOrganizationsStatistics';
import { useEffect, useState } from 'react';

interface FeedbackPollingProps {
  statistics: StatisticsProps;
}

export function useFeedbackPolling({ statistics }: FeedbackPollingProps) {
  const [feedbackDiff, setFeedbackDiff] = useState(0);
  const { organizationId } = useOrganizationId();
  const { showToast } = useToast();

  const { refetch: refetchCurrentStatistics } = useUserOrganizationsStatistics({
    organizationId,
    queryKey: QUERY_KEYS.organizationPollingStatistics(organizationId),
  });

  useEffect(() => {
    const interval = setInterval(async () => {
      const { data: newStatistics } = await refetchCurrentStatistics();
      if (!newStatistics || !newStatistics.totalCount || !statistics) return;
      const diff =
        Number(newStatistics.totalCount) - Number(statistics.totalCount);

      if (diff !== 0) {
        setFeedbackDiff(diff);
      }
    }, 5000);

    return () => clearInterval(interval);
  }, [statistics, refetchCurrentStatistics]);

  useEffect(() => {
    if (feedbackDiff > 0) {
      showToast(
        `새로운 피드백 ${feedbackDiff}건이 도착했습니다! 새로고침 해주세요.`,
        'origin',
        5000
      );
    }
  }, [feedbackDiff]);

  return { feedbackDiff, setFeedbackDiff };
}
