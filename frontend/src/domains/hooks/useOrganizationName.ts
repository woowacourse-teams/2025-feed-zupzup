import { getOrganizationName } from '@/apis/organization.api';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [groupName, setGroupName] = useState('피드줍줍');
  const [totalCheeringCount, setTotalCheeringCount] = useState(0);

  useEffect(() => {
    async function getOrganization() {
      const response = await getOrganizationName({ organizationId: 1 });
      setGroupName(response!.data.organizationName);
      setTotalCheeringCount(response!.data.totalCheeringCount);
    }

    getOrganization();
  }, []);

  return { groupName, totalCheeringCount };
}
