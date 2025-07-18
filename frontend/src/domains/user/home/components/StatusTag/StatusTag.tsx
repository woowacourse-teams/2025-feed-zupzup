import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './StatusTag.styles';

export interface StatusTagProps {
  children: React.ReactNode;
  type: StatusType;
}

export type StatusType = 'complete' | 'incomplete';

export default function StatusTag({
  children,
  type = 'incomplete',
}: StatusTagProps) {
  const theme = useAppTheme();

  return <Tag customCss={container(theme, type)}>{children}</Tag>;
}
