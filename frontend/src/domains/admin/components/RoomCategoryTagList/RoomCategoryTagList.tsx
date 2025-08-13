import { CategoryListType } from '@/constants/categoryList';
import { MAX_CATEGORIES } from '@/constants/maxCategories';
import RoomCategoryTag from '@/domains/admin/components/RoomCategoryTag/RoomCategoryTag';
import {
  categoryCountText,
  roomCategoryTagContainer,
  roomCategoryTagListContainer,
  roomCategoryTagListText,
} from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface RoomCategoryTagListProps {
  selectedCategories: {
    icon: string | React.ReactNode;
    category: CategoryListType;
  }[];
  handleCategoryTagClick: (category: CategoryListType) => void;
}

export default function RoomCategoryTagList({
  selectedCategories,
  handleCategoryTagClick,
}: RoomCategoryTagListProps) {
  const theme = useAppTheme();

  return (
    <div css={roomCategoryTagListContainer}>
      <div css={roomCategoryTagListText}>
        <p>선택된 카테고리</p>
        <p css={categoryCountText(theme)}>
          {selectedCategories.length}/{MAX_CATEGORIES}
        </p>
      </div>
      <div css={roomCategoryTagContainer(theme)}>
        {selectedCategories.map((item) => (
          <RoomCategoryTag
            key={item.category}
            category={item.category}
            icon={item.icon}
            onDeleteClick={() => {
              handleCategoryTagClick(item.category);
            }}
          />
        ))}
      </div>
    </div>
  );
}
