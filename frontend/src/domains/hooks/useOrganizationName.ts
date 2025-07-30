import { getOrganizationName } from '@/apis/organization.api';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [placeName, setPlaceName] = useState('피드줍줍');

  useEffect(() => {
    async function getOrganization() {
      const response = await getOrganizationName({ organizationId: 1 });
      setPlaceName(response!.data.organizationName);
    }

    getOrganization();
  }, []);

  return { placeName };
}
