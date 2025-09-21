import { CATEGORY_LIST, CategoryListType } from '@/constants/categoryList';
import { SelectedCategoryItem } from '../admin/CreateRoomModal/hooks/useCategorySelection';

export function createCategoryIconPairs(initialCategories: CategoryListType[]) {
  return initialCategories.reduce(
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
}
