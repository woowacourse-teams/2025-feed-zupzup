import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './Tag.styles';
import { SerializedStyles } from '@emotion/react';

export interface TagProps {
  children: React.ReactNode;
  customCSS?: SerializedStyles;
}

export default function Tag({ children, customCSS }: TagProps) {
  const theme = useAppTheme();

  return <div css={[container(theme), customCSS]}>{children}</div>;
}
