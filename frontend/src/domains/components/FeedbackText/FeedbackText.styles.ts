import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'CONFIRMED' | 'WAITING'
) => css`
  ${theme.typography.inter.caption}

  line-height: 20px;

  ${type === 'WAITING'
    ? `color : ${theme.colors.darkGray[100]}`
    : `color : ${theme.colors.gray[300]}`}
`;
