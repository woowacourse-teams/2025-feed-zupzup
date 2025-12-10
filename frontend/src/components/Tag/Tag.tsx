import { useAppTheme } from '@/hooks/useAppTheme';
import { container } from './Tag.styles';
import { SerializedStyles } from '@emotion/react';

export interface TagProps {
  children: React.ReactNode;
  onClick?: () => void;
  customCSS?: SerializedStyles;
}

export default function Tag({
  children,
  onClick,
  customCSS,
  ...props
}: TagProps) {
  const theme = useAppTheme();

  return (
    <div css={[container(theme), customCSS]} onClick={onClick} {...props}>
      {children}
    </div>
  );
}
