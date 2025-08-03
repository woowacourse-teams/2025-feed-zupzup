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
`;

export const day = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[500]};
`;

export const content = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.inter.small}
`;

export const confirmedText = (theme: Theme) => css`
  /* color: ${theme.colors.white[100]}; */
  background-color: ${theme.colors.green[100]}22;
  border: 1px solid ${theme.colors.green[100]};
`;
