import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'CONFIRMED' | 'WAITING'
) => css`
  ${theme.typography.inter.small}

  line-height: 20px;
  ${type === 'CONFIRMED' && `color : ${theme.colors.gray[500]}`}
`;
