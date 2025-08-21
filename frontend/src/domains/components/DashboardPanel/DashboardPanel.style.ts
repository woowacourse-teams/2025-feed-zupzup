import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const dashboard = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 16px;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;

export const topContainer = css`
  display: flex;
  justify-content: space-between;
`;

export const button = css`
  cursor: pointer;
`;

export const dotLayout = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const dot = (theme: Theme, color?: string) => css`
  width: 8px;
  height: 8px;
  background-color: ${color ?? theme.colors.gray[600]};
  border-radius: 50%;
`;

export const panelTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  color: ${theme.colors.gray[600]};
`;

export const panelContent = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.h2};

  font-weight: 900;
`;

export const captionContent = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  color: ${theme.colors.gray[600]};
`;
