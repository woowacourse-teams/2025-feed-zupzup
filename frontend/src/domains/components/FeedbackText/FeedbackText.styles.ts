import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'CONFIRMED' | 'WAITING'
) => css`
  ${theme.typography.pretendard.caption}

  white-space: pre-wrap;
  word-break: break-all;
  overflow-wrap: break-word;
  overflow: hidden;

  ${type === 'WAITING'
    ? `color : ${theme.colors.darkGray[100]}`
    : `color : ${theme.colors.gray[300]}`}
`;
