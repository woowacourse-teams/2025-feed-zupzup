import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const overlay = css`
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(0 0 0 / 50%);
`;

export const modal = (
  theme: Theme,
  width: number = 300,
  height?: number
) => css`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 24px;
  width: ${width}px;
  min-height: ${height}px;
  padding: 24px;
  background-color: ${theme.colors.white[100]};
  border-radius: 8px;
  box-shadow: 0 10px 25px rgb(0 0 0 / 10%);
`;

export const content = css`
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.inter.h4};

  color: ${theme.colors.black[100]};
`;

export const message = (theme: Theme) => css`
  ${theme.typography.inter.bodyRegular};

  color: ${theme.colors.gray[600]};
`;

export const buttonContainer = css`
  display: flex;
  justify-content: center;
  gap: 12px;
`;
