import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const header = css`
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 64px;
  padding: 20px;
  background-color: white;
`;

export const headerSection = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const headerTitle = (theme: Theme) => css`
  font-size: 14px;
  color: ${theme.colors.darkGray[400]};
`;

export const headerSubtitle = (theme: Theme) => css`
  color: ${theme.colors.gray[600]};
`;

export const captionSection = css`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;
