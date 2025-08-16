import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const toastStyle = (theme: Theme) => css`
  position: fixed;
  top: 96px;
  left: 50%;
  z-index: 9999;
  padding: 10px 20px;
  font-size: 14px;
  color: #fff;
  background-color: ${theme.colors.red[50]};
  border-radius: 9999px;
  transform: translateX(-50%);
`;
