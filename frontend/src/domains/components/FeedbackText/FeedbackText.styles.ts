import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'CONFIRMED' | 'WAITING'
) => css`
  ${theme.typography.pretendard.caption}

  line-height: 24px;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-wrap: break-word;

  ${type === 'WAITING'
    ? `color : ${theme.colors.darkGray[100]}`
    : `color : ${theme.colors.gray[300]}`}
`;
