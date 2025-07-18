import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './CategoryTag.styles';

export interface CategoryTagProps {
  text: string;
  type: 'complete' | 'incomplete';
}

export type CategoryType = 'complete' | 'incomplete';

export default function CategoryTag({
  text,
  type = 'incomplete',
}: CategoryTagProps) {
  const theme = useAppTheme();

  return <Tag customCss={container(theme, type)}>{text}</Tag>;
}
