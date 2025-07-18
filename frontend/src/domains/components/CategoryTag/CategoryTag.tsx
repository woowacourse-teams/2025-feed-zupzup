import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './CategoryTag.styles';
import { Type } from '@/types/feedbackStatus.types';

export interface CategoryTagProps {
  text: string;
  type: Type;
}

export default function CategoryTag({
  text,
  type = 'incomplete',
}: CategoryTagProps) {
  const theme = useAppTheme();

  return <Tag customCSS={container(theme, type)}>{text}</Tag>;
}
