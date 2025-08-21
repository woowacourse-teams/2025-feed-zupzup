import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const roomNameInput = (theme: Theme) => css`
  width: 100%;
  margin-top: 8px;
  padding: 8px;
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 8px;
`;
