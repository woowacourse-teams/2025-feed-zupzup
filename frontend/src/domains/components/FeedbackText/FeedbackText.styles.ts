import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'complete' | 'incomplete'
) => css`
  ${theme.typography.inter.small}

  line-height: 20px;
  ${type === 'complete' && `color : ${theme.colors.gray[500]}`}
`;
