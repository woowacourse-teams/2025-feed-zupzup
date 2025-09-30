import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const moreMenuContainer = (theme: Theme) => css`
  position: relative;

  :hover {
    background-color: ${theme.colors.darkGray[100]};
    border-radius: 50%;
  }
`;

export const moreMenu = css`
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
`;

export const MoreButton = css`
  width: 50px;
  height: 50px;
  text-align: center;
`;
