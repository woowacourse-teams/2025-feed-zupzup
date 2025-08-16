import { useQuery } from '@tanstack/react-query';
import { getOrganizationStatistics } from '@/apis/organization.api';
import type { GetOrganizationStatistics } from '@/types/organization.types';
import { QUERY_KEYS } from '@/constants/queryKeys';

export interface StatisticsProps {
  reflectionRate: string;
  confirmedCount: string;
  waitingCount: string;
  totalCount: string;
}

const EMPTY: StatisticsProps = {
  reflectionRate: '0',
  confirmedCount: '0',
  waitingCount: '0',
  totalCount: '0',
};

export default function useUserOrganizationsStatistics() {
  const { data = EMPTY } = useQuery({
    queryKey: QUERY_KEYS.organizationStatistics(1),
    queryFn: () => getOrganizationStatistics({ organizationId: 1 }),
    select: (res: GetOrganizationStatistics) => res.data,
  });

  return { statistics: data };
}
