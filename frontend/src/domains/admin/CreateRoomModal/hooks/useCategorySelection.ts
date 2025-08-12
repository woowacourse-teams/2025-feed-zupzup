import { useState, useCallback } from 'react';
import { CategoryListType } from '@/constants/categoryList';

export interface SelectedCategoryItem {
  icon: string | React.ReactNode;
  category: CategoryListType;
}

export function useCategorySelection(max = 4) {
  const [selectedCategories, setSelectedCategories] = useState<
    SelectedCategoryItem[]
  >([]);

  const handleCategoryClick = useCallback(
    (icon: React.ReactNode, category: CategoryListType) => {
      setSelectedCategories((prev) => {
        const exists = prev.some((item) => item.category === category);

        if (exists) return prev.filter((item) => item.category !== category);

        if (prev.length >= max) {
          alert(`카테고리는 최대 ${max}개까지만 선택할 수 있습니다.`);
          return prev;
        }

        return [...prev, { icon, category }];
      });
    },
    [max]
  );

  const handleCategoryTagClick = useCallback((category: CategoryListType) => {
    setSelectedCategories((prev) =>
      prev.filter((item) => item.category !== category)
    );
  }, []);

  return { selectedCategories, handleCategoryClick, handleCategoryTagClick };
}
