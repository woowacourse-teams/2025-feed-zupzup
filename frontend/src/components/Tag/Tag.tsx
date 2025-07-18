import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './Tag.styles';
import { SerializedStyles } from '@emotion/react';

export interface TagProps {
  children: React.ReactNode;
  customCss?: SerializedStyles;
}

export default function Tag({ children, customCss }: TagProps) {
  const theme = useAppTheme();

  return <div css={[container(theme), customCss]}>{children}</div>;
}
