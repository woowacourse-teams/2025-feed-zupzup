import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const banner = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 32px;
  justify-content: center;
  text-align: start;
  background-color: ${theme.colors.gray[100]};
  border-radius: 16px;
`;
