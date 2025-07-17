import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css, SerializedStyles } from '@emotion/react';

interface TagProps {
  children: React.ReactNode;
  customCss?: SerializedStyles;
}

export default function Tag({ children, customCss }: TagProps) {
  const theme = useAppTheme();

  return <div css={[container(theme), customCss]}>{children}</div>;
}

const container = (theme: Theme) => css`
  display: inline-block;
  padding: 5px 10px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 20px;

  ${theme.typography.inter.caption};
`;
