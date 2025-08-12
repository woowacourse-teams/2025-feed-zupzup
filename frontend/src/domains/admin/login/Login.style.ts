import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const login = (theme: Theme) => css`
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
  padding: 64px ${PAGE_PADDING_PX * 2}px 0 ${PAGE_PADDING_PX * 2}px;
  background-color: ${theme.colors.blue[100]};
`;

export const fieldContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  margin-bottom: 16px;
`;

export const loginContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 16px;
  width: 100%;
  color: ${theme.colors.white[100]};
`;

export const loginText = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};
`;

export const loginDescription = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  color: ${theme.colors.white[100]}88;
`;

export const loginForm = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  gap: 24px;
  width: calc(100% + ${PAGE_PADDING_PX * 4}px);
  height: 100%;
  margin: -${PAGE_PADDING_PX}px;
  padding: ${PAGE_PADDING_PX * 2}px;
  background-color: ${theme.colors.white[100]};
  border-radius: 16px;
`;

export const loginCaptionContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
  width: 100%;
  margin-top: 16px;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption};

  strong {
    color: ${theme.colors.purple[100]};
    cursor: pointer;
  }
`;
