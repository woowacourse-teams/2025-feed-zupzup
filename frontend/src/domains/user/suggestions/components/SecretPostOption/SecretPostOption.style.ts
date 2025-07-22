import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const secretOption = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 16px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 16px;
`;

export const secretText = (theme: Theme) => css`
  color: ${theme.colors.darkGray[200]};
  ${theme.typography.inter.small}
`;
