import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const formField = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
`;

export const fieldLabel = (theme: Theme) => css`
  color: ${theme.colors.darkGray[100]};

  ${theme.typography.BMHANNAAir.caption}
`;
