import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const authLayout = (theme: Theme) => css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 36px;
  width: 100%;
  height: 100%;
  padding: 64px ${PAGE_PADDING_PX / 2}px 0 ${PAGE_PADDING_PX / 2}px;
  background-color: ${theme.colors.blue[100]};
`;

export const titleContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 16px;
  width: 100%;
  color: ${theme.colors.white[100]};
`;

export const authTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};

  &:focus {
    outline: none;
  }
`;

export const authDescription = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  color: ${theme.colors.white[100]}88;
`;
