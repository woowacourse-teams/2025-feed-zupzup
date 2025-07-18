import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const secretOption = (theme: Theme) => css`
  background-color: ${theme.colors.gray[100]};
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  width: 100%;
  border-radius: 16px;
`;

export const secretText = (theme: Theme) => css`
  color: ${theme.colors.darkGray[200]};
  ${theme.typography.inter.small}
`;
