import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const banner = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 24px;
  text-align: start;
  background-color: ${theme.colors.gray[100]};
  border-radius: 16px;
`;

export const bannerDescription = (theme: Theme) => css`
  color: ${theme.colors.darkGray[100]};
`;
