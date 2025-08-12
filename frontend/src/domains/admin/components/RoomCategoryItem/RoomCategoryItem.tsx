import Button from '@/components/@commons/Button/Button';
import { CategoryListType } from '@/constants/categoryList';
import {
  categoryItemContainer,
  categoryStyle,
  iconStyle,
} from '@/domains/admin/components/RoomCategoryItem/RoomCategoryItem.styles';

interface RoomCategoryItemProps {
  icon: string | React.ReactNode;
  category: string;
  isSelected: boolean;
  onClick: (icon: string | React.ReactNode, category: CategoryListType) => void;
}

export default function RoomCategoryItem({
  icon,
  category,
  isSelected,
  onClick,
}: RoomCategoryItemProps) {
  return (
    <Button
      css={categoryItemContainer(isSelected)}
      onClick={() => onClick(icon, category as CategoryListType)}
    >
      <p css={iconStyle}>{icon}</p>
      <p css={categoryStyle}>{category}</p>
    </Button>
  );
}
