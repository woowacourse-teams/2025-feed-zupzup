import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { CategoryTagProps } from './CategoryTag.types';
import { container } from './CategoryTag.styles';

export default function CategoryTag({
  text,
  type = 'incomplete',
}: CategoryTagProps) {
  const theme = useAppTheme();

  return <Tag customCss={container(theme, type)}>{text}</Tag>;
}
