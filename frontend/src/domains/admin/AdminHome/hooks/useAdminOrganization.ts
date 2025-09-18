import {
  AdminOrganizationType,
  getAdminOrganization,
} from '@/apis/adminOrganization.api';
import { ApiError } from '@/apis/apiClient';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';

interface UseAdminOrganizationParams {
  adminName: string;
}

export default function useAdminOrganization({
  adminName,
}: UseAdminOrganizationParams) {
  const { handleApiError } = useApiErrorHandler();

  const {
    data: adminOrganizations = [],
    isLoading,
    isError,
    error,
  } = useQuery<AdminOrganizationType[]>({
    queryKey: QUERY_KEYS.adminOrganizations(adminName),
    queryFn: async () => {
      const res = await getAdminOrganization();
      return res.data;
    },
    retry: false,
    staleTime: 5 * 60 * 1000,
    gcTime: 15 * 60 * 1000,
  });

  useEffect(() => {
    if (isError) {
      handleApiError(error as ApiError);
    }
  }, [isError, handleApiError]);

  return { adminOrganizations, isLoading };
}
