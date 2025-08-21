import { useState, useCallback, useEffect } from 'react';
import { CategoryListType, CATEGORY_LIST } from '@/constants/categoryList';
import { MAX_CATEGORIES } from '@/constants/maxCategories';

export interface SelectedCategoryItem {
  icon: string | React.ReactNode;
  category: CategoryListType;
}

interface UseCategorySelectionProps {
  initialCategories?: CategoryListType[];
}

export function useCategorySelection({
  initialCategories,
}: UseCategorySelectionProps = {}) {
  const [selectedCategories, setSelectedCategories] = useState<
    SelectedCategoryItem[]
  >([]);

  useEffect(() => {
    if (!initialCategories || initialCategories.length === 0) return;

    const initialSelectedCategories: SelectedCategoryItem[] =
      initialCategories.reduce(
        (acc: SelectedCategoryItem[], categoryString) => {
          const categoryItem = CATEGORY_LIST.find(
            (item) => item.category === categoryString
          );
          if (categoryItem) {
            acc.push({
              icon: categoryItem.icon,
              category: categoryItem.category,
            });
          }
          return acc;
        },
        []
      ) as SelectedCategoryItem[];

    setSelectedCategories(initialSelectedCategories);
  }, [initialCategories]);

  const handleCategoryClick = useCallback(
    (icon: React.ReactNode | string, category: CategoryListType) => {
      setSelectedCategories((prev) => {
        const exists = prev.some((item) => item.category === category);

        if (exists) return prev.filter((item) => item.category !== category);

        if (prev.length >= MAX_CATEGORIES) {
          alert(
            `카테고리는 최대 ${MAX_CATEGORIES}개까지만 선택할 수 있습니다.`
          );
          return prev;
        }

        return [...prev, { icon, category }];
      });
    },
    [MAX_CATEGORIES]
  );

  const handleCategoryTagClick = useCallback((category: CategoryListType) => {
    setSelectedCategories((prev) =>
      prev.filter((item) => item.category !== category)
    );
  }, []);

  return { selectedCategories, handleCategoryClick, handleCategoryTagClick };
}
