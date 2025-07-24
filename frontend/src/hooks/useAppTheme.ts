import { useTheme } from '@emotion/react';
import { Theme } from '../theme';

export const useAppTheme = (): Theme => {
  return useTheme() as Theme;
};
