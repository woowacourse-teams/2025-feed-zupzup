import { SelectedCategoryItem } from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
import { useEffect, useState } from 'react';

interface UseSubmitDisabledProps {
  organizationName: string;
  selectedCategories: SelectedCategoryItem[];
}

export default function useSubmitDisabled({
  organizationName,
  selectedCategories,
}: UseSubmitDisabledProps) {
  const [disabled, setDisabled] = useState(true);

  useEffect(() => {
    setDisabled(!organizationName || selectedCategories.length === 0);
  }, [organizationName, selectedCategories]);

  return { disabled };
}
