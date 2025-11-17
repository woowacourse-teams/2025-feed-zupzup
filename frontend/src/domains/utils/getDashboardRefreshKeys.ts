import { QUERY_KEYS } from '@/constants/queryKeys';

interface GetDashboardRefreshKeys {
  organizationId: string;
}

export const getDashboardRefreshKeys = ({
  organizationId,
}: GetDashboardRefreshKeys) => [
  QUERY_KEYS.organizationStatistics(organizationId),
  QUERY_KEYS.organizationPollingStatistics(organizationId),
  QUERY_KEYS.infiniteFeedbacks,
  QUERY_KEYS.aiSummary(organizationId),
];
