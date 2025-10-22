import {
  AdminOrganizationType,
  getAdminOrganization,
} from '@/apis/adminOrganization.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

interface UseAdminOrganizationParams {
  adminName: string;
}

export default function useAdminOrganization({
  adminName,
}: UseAdminOrganizationParams) {
  const { data: adminOrganizations = [], isLoading } = useQuery<
    AdminOrganizationType[]
  >({
    queryKey: QUERY_KEYS.adminOrganizations(adminName),
    queryFn: async () => {
      const res = await getAdminOrganization();
      return res.data;
    },
    retry: false,
    staleTime: 5 * 60 * 1000,
    gcTime: 15 * 60 * 1000,
  });

  return { adminOrganizations, isLoading };
}
