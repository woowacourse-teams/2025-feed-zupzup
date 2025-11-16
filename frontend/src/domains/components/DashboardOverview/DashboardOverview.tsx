import { useFeedbackPolling } from '@/domains/hooks/useFeedbackPolling';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useUserOrganizationsStatistics from '@/domains/hooks/useUserOrganizationsStatistics';
import { getDashboardRefreshKeys } from '@/domains/utils/getDashboardRefreshKeys';
import { useQueryClient } from '@tanstack/react-query';
import { memo, useCallback } from 'react';
import DashboardPanelContent from './DashboardPanelContent/DashboardPanelContent';
import OverviewHeader from './OverviewHeader/OverviewHeader';

export default memo(function DashboardOverview() {
  const { organizationId } = useOrganizationId();
  const queryClient = useQueryClient();
  const { statistics } = useUserOrganizationsStatistics({
    organizationId,
  });

  const { feedbackDiff } = useFeedbackPolling({
    statistics,
  });

  const handleRefresh = useCallback(() => {
    getDashboardRefreshKeys({ organizationId }).forEach((key) => {
      queryClient.invalidateQueries({ queryKey: key });
    });
  }, [organizationId]);

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
