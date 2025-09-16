import { getAdminAuth } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';

export default function useAdminAuth() {
  const { handleApiError } = useApiErrorHandler();

  const {
    data: adminAuth,
    isError,
    error,
  } = useQuery({
    queryKey: [QUERY_KEYS.adminAuth],
    queryFn: getAdminAuth,
    staleTime: Infinity,
    gcTime: Infinity,
  });

  if (isError) {
    handleApiError(error as ApiError);
  }

  return { adminAuth };
}
