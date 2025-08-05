import { GetOrganizationStatistics } from './../../types/organization.types';
import { getOrganizationStatistics } from '@/apis/organization.api';
import { useEffect, useState } from 'react';

export interface StatisticsProps {
  reflectionRate: string;
  confirmedCount: string;
  waitingCount: string;
  totalCount: string;
}

export default function useUserOrganizationsStatistics() {
  const [statistics, setStatistics] = useState<StatisticsProps>({
    reflectionRate: '0',
    confirmedCount: '0',
    waitingCount: '0',
    totalCount: '0',
  });

  useEffect(() => {
    const getData = async () => {
      const response = (await getOrganizationStatistics({
        organizationId: 1,
        period: 'WEEK',
      })) as GetOrganizationStatistics;
      setStatistics(response.data);
    };

    getData();
  }, []);

  return { statistics };
}
