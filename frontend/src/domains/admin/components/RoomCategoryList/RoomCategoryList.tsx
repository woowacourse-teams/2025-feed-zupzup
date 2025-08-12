import { CATEGORY_LIST } from '@/constants/categoryList';
import RoomCategoryItem from '@/domains/admin/components/RoomCategoryItem/RoomCategoryItem';
import { categoryListContainer } from '@/domains/admin/components/RoomCategoryList/RoomCategoryList.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function RoomCategoryList() {
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
            isSelected={false}
            onClick={() => {}}
          />
        ))}
      </div>
    </div>
  );
}
