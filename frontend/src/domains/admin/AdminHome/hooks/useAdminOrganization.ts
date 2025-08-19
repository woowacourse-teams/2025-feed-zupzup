import {
  AdminOrganization,
  getAdminOrganization,
  GetAdminOrganizationResponse,
} from '@/apis/adminOrganization.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useAdminOrganization() {
  const [adminOrganizations, setAdminOrganizations] = useState<
    AdminOrganization[] | []
  >([]);
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    const fetchAdminOrganizations = async () => {
      try {
        const response: GetAdminOrganizationResponse =
          await getAdminOrganization();
        setAdminOrganizations(response.data);
      } catch (error) {
        showErrorModal(error, '조직 정보 조회 실패');
      }
    };
    fetchAdminOrganizations();
  }, []);

  return { adminOrganizations };
}
