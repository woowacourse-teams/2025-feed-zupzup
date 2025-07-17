import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { StatusTagProps } from './StatusTag.types';
import { container } from './StatusTag.styles';

export default function StatusTag({
  children,
  type = 'incomplete',
}: StatusTagProps) {
  const theme = useAppTheme();

  return <Tag customCss={container(theme, type)}>{children}</Tag>;
}
