import { getOrganizationName } from '@/apis/organization.api';
import { CategoryListType } from '@/constants/categoryList';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

interface UseOrganizationNameProps {
  organizationId: string;
}

export default function useOrganizationName({
  organizationId,
}: UseOrganizationNameProps) {
  const [groupName, setGroupName] = useState('피드줍줍');
  const [totalCheeringCount, setTotalCheeringCount] = useState(0);
  const [categories, setCategories] = useState<CategoryListType[]>([]);
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    async function getOrganization() {
      try {
        const response = await getOrganizationName({ organizationId });
        setGroupName(response!.data.organizationName);
        setTotalCheeringCount(response!.data.totalCheeringCount);
        setCategories(response!.data.categories);
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }

    getOrganization();
  }, []);

  return { groupName, totalCheeringCount, categories };
}
