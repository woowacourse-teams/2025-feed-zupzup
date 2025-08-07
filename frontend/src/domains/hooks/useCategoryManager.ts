import { Analytics, onboardingEvents } from '@/analytics';
import { CategoryType } from '@/analytics/types';
import { useState } from 'react';

interface useCategoryManagerProps {
  moveNextStep: () => void;
}

export default function useCategoryManager({
  moveNextStep,
}: useCategoryManagerProps) {
  const [category, setCategory] = useState<CategoryType | null>(null);
  const handleCategoryChange = (newCategory: CategoryType) => {
    Analytics.track(onboardingEvents.categorySelect(newCategory));

    setCategory(newCategory);
    moveNextStep();
  };

  return {
    category,
    handleCategoryChange,
  };
}
