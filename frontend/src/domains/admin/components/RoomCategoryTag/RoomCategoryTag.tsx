import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './RoomCategoryTag.styles';
import Button from '@/components/@commons/Button/Button';
import { CategoryListType } from '@/constants/categoryList';

export interface CategoryTagProps {
  category: CategoryListType;
  icon: string | React.ReactNode;
  onDeleteClick: () => void;
}

export default function RoomCategoryTag({
  category,
  icon,
  onDeleteClick,
}: CategoryTagProps) {
  const theme = useAppTheme();

  return (
    <Tag customCSS={container(theme)} onClick={onDeleteClick}>
      <p>{icon}</p>
      <p>{category}</p>
      <Button onClick={onDeleteClick}>&times;</Button>
    </Tag>
  );
}
