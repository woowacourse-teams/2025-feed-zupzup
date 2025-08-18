import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const editRoomModalContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

export const editRoomModalTitle = css`
  text-align: center;
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  gap: 10px;

  ${theme.typography.pretendard.caption}
`;
