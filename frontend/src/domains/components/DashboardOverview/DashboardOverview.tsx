import OverviewHeader from './OverviewHeader/OverviewHeader';
import DashboardPanelContent from './DashboardPanelContent/DashboardPanelContent';
import { memo } from 'react';
import useUserOrganizationsStatistics from '@/domains/hooks/useUserOrganizationsStatistics';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useFeedbackPolling } from '@/domains/hooks/useFeedbackPolling';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQueryClient } from '@tanstack/react-query';

export default memo(function DashboardOverview() {
  const { organizationId } = useOrganizationId();
  const { statistics } = useUserOrganizationsStatistics({
    organizationId,
  });
  const queryClient = useQueryClient();

  const { feedbackDiff, setFeedbackDiff } = useFeedbackPolling({
    statistics,
  });

  const refreshStatistics = () => {
    return [
      QUERY_KEYS.organizationStatistics(organizationId),
      QUERY_KEYS.organizationPollingStatistics(organizationId),
      QUERY_KEYS.infiniteFeedbacks,
      ['aiSummaryDetail', organizationId],
    ];
  };

  const handleRefresh = () => {
    setFeedbackDiff(0);
    const keys = refreshStatistics();
    keys.forEach((key) => {
      queryClient.invalidateQueries({ queryKey: key });
    });
  };

  return (
    <>
      <OverviewHeader
        feedbackDiff={feedbackDiff}
        handleRefresh={handleRefresh}
      />
      <DashboardPanelContent statistics={statistics} />
    </>
  );
});
