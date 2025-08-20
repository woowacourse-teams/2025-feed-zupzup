import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const roomModalContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

export const roomModalTitle = css`
  text-align: center;
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  gap: 10px;

  ${theme.typography.pretendard.caption}
`;
