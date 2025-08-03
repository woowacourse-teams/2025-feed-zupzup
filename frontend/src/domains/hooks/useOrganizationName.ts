import { getOrganizationName } from '@/apis/organization.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [groupName, setGroupName] = useState('피드줍줍');
  const [totalCheeringCount, setTotalCheeringCount] = useState(0);
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    async function getOrganization() {
      try {
        const response = await getOrganizationName({ organizationId: 1 });
        setGroupName(response!.data.organizationName);
        setTotalCheeringCount(response!.data.totalCheeringCount);
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }

    getOrganization();
  }, []);

  return { groupName, totalCheeringCount };
}
