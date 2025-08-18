import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const roomCategoryTagListContainer = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const roomCategoryTagContainer = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 10px;
  height: 50px;
  padding: 5px;
  background-color: ${theme.colors.gray[100]}a7;
  border-radius: 8px;
  overflow: auto;
`;

export const roomCategoryTagListText = css`
  display: flex;
  gap: 10px;
`;

export const categoryCountText = (theme: Theme) => css`
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.captionSmall}
`;
