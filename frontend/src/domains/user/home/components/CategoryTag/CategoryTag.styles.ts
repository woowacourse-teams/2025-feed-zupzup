import { Theme } from '@/theme';
import { CategoryType } from './CategoryTag.types';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: CategoryType) => css`
  background-color: ${type === 'incomplete'
    ? theme.colors.yellow[200]
    : theme.colors.gray[200]};
`;
