import { Analytics, onboardingEvents } from '@/analytics';
import { CategoryListType } from '@/constants/categoryList';
import { useState } from 'react';

interface useCategoryManagerProps {
  moveNextStep: () => void;
}

export default function useCategoryManager({
  moveNextStep,
}: useCategoryManagerProps) {
  const [category, setCategory] = useState<CategoryListType | null>(null);
  const handleCategoryChange = (newCategory: CategoryListType) => {
    Analytics.track(onboardingEvents.categorySelect(newCategory));

    setCategory(newCategory);
    moveNextStep();
  };

  return {
    category,
    handleCategoryChange,
  };
}
