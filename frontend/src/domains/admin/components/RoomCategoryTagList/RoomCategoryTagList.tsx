import { CategoryListType } from '@/constants/categoryList';
import RoomCategoryTag from '@/domains/admin/components/RoomCategoryTag/RoomCategoryTag';
import {
  roomCategoryTagContainer,
  roomCategoryTagListContainer,
} from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface RoomCategoryTagListProps {
  selectedCategory: {
    icon: string | React.ReactNode;
    category: CategoryListType;
  }[];
  handleCategoryTagClick: (category: CategoryListType) => void;
}

export default function RoomCategoryTagList({
  selectedCategory,
  handleCategoryTagClick,
}: RoomCategoryTagListProps) {
  const theme = useAppTheme();

  return (
    <div css={roomCategoryTagListContainer}>
      <p>선택된 카테고리</p>
      <div css={roomCategoryTagContainer(theme)}>
        {selectedCategory.map((item) => (
          <RoomCategoryTag
            key={item.category}
            category={item.category}
            icon={item.icon}
            onDeleteClick={() => {
              handleCategoryTagClick(item.category);
            }}
          ></RoomCategoryTag>
        ))}
      </div>
    </div>
  );
}
