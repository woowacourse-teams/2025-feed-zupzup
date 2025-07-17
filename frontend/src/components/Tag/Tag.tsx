import { useAppTheme } from '@/hooks/useAppTheme';
import { TagProps } from './Tag.types';
import { container } from './Tag.styles';

export default function Tag({ children, customCss }: TagProps) {
  const theme = useAppTheme();

  return <div css={[container(theme), customCss]}>{children}</div>;
}
