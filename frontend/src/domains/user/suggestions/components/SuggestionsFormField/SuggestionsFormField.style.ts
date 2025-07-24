import { css } from '@emotion/react';
import { theme } from '@/theme';

export const formField = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
`;

export const fieldLabel = css`
  color: ${theme.colors.darkGray[200]};
`;
