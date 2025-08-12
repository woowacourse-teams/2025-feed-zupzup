import { CATEGORY_LIST, CategoryListType } from '@/constants/categoryList';
import RoomCategoryItem from '@/domains/admin/components/RoomCategoryItem/RoomCategoryItem';
import { categoryListContainer } from '@/domains/admin/components/RoomCategoryList/RoomCategoryList.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface RoomCategoryListProps {
  selectedCategories: {
    icon: string | React.ReactNode;
    category: CategoryListType;
  }[];
  handleCategorySelect: (
    icon: string | React.ReactNode,
    category: CategoryListType
  ) => void;
}

export default function RoomCategoryList({
  selectedCategories,
  handleCategorySelect,
}: RoomCategoryListProps) {
  const theme = useAppTheme();

  return (
    <div>
      <p>카테고리 선택</p>
      <div css={categoryListContainer(theme)}>
        {CATEGORY_LIST.map(({ icon, category }) => (
          <RoomCategoryItem
            key={category}
            icon={icon}
            category={category}
            isSelected={selectedCategories.some(
              (item) => item.category === category
            )}
            onClick={handleCategorySelect}
          />
        ))}
      </div>
    </div>
  );
}
