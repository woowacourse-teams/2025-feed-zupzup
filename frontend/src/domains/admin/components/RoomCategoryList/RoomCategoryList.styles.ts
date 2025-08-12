import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const categoryListContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 160px;
  margin-top: 8px;
  padding: 16px;
  background-color: ${theme.colors.gray[100]}a7;
  border-radius: 8px;
  overflow: auto;
`;
