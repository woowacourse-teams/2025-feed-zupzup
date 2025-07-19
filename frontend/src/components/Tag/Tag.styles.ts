import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = (theme: Theme) => css`
  display: inline-block;
  padding: 4px 10px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 20px;

  ${theme.typography.inter.caption};
`;
