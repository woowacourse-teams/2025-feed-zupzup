import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  display: flex;
  justify-content: space-between;
`;

export const calendar = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 8px;
  color: ${theme.colors.gray[600]};
  vertical-align: middle;
`;

export const day = css`
  line-height: 1;
`;

export const content = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.inter.small}
`;
