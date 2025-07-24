import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './CategoryTag.styles';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface CategoryTagProps {
  text: string;
  type: FeedbackStatusType;
}

export default function CategoryTag({
  text,
  type = 'WAITING',
}: CategoryTagProps) {
  const theme = useAppTheme();

  return <Tag customCSS={container(theme, type)}>{text}</Tag>;
}
