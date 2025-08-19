import {
  AdminOrganization,
  getAdminOrganization,
  GetAdminOrganizationResponse,
} from '@/apis/adminOrganization.api';
import { ApiError } from '@/apis/apiClient';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { useEffect, useState } from 'react';

export default function useAdminOrganization() {
  const [adminOrganizations, setAdminOrganizations] = useState<
    AdminOrganization[] | []
  >([]);
  const { handleApiError } = useApiErrorHandler();

  useEffect(() => {
    const fetchAdminOrganizations = async () => {
      try {
        const response: GetAdminOrganizationResponse =
          await getAdminOrganization();
        setAdminOrganizations(response.data);
      } catch (error) {
        handleApiError(error as ApiError);
      }
    };
    fetchAdminOrganizations();
  }, []);

  return { adminOrganizations };
}
