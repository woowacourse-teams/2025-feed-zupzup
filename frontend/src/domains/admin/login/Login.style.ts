import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const login = (theme: Theme) => css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: -${PAGE_PADDING_PX}px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: calc(100% + ${PAGE_PADDING_PX}px);
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.blue[100]};
`;

export const loginContainer = () => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const loginText = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small};
`;

export const loginDescription = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};
`;

export const loginForm = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  max-width: 400px;
  padding: ${PAGE_PADDING_PX}px;
  background-color: ${theme.colors.white[100]};
  border-radius: 8px;
`;
