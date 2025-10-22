import { getAdminAuth } from '@/apis/admin.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

export default function useAdminAuth() {
  const { data: adminAuth, isLoading } = useQuery({
    queryKey: QUERY_KEYS.adminAuth,
    queryFn: getAdminAuth,
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
  });

  return { adminAuth, isLoading };
}
