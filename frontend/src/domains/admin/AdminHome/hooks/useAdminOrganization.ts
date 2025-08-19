import {
  AdminOrganization,
  getAdminOrganization,
} from '@/apis/adminOrganization.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';

export default function useAdminOrganization() {
  const { handleApiError } = useApiErrorHandler();

  const {
    data: adminOrganizations = [],
    isLoading,
    isError,
    error,
  } = useQuery<AdminOrganization[]>({
    queryKey: QUERY_KEYS.adminOrganizations(),
    queryFn: async () => {
      const res = await getAdminOrganization();
      return res.data;
    },
  });

  useEffect(() => {
    if (isError) {
      handleApiError(error as ApiError);
    }
  }, [isError, handleApiError]);

  return { adminOrganizations, isLoading };
}
