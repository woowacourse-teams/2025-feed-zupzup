import { getOrganizationName } from '@/apis/organization.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [placeName, setPlaceName] = useState('피드줍줍');
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    async function getOrganization() {
      try {
        const response = await getOrganizationName({ organizationId: 1 });
        setPlaceName(response!.data.organizationName);
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }

    getOrganization();
  }, []);

  return { placeName };
}
