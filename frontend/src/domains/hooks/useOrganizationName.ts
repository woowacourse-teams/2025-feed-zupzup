import { getOrganizationName } from '@/apis/organization.api';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [groupName, setGroupName] = useState('피드줍줍');

  useEffect(() => {
    async function getOrganization() {
      const response = await getOrganizationName({ organizationId: 1 });
      setGroupName(response!.data.organizationName);
    }

    getOrganization();
  }, []);

  return { groupName };
}
