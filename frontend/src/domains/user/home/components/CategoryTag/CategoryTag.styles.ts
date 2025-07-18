import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { CategoryType } from './CategoryTag';

export const container = (theme: Theme, type: CategoryType) => css`
  background-color: ${type === 'incomplete'
    ? theme.colors.yellow[200]
    : theme.colors.gray[200]};
`;
