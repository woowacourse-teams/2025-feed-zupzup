import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const dashboard = (theme: Theme, isClick: boolean) => css`
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-sizing: border-box;
  width: 100%;
  padding: 16px;
  background-color: ${theme.colors.white[300]};
  border: 2px solid ${isClick ? theme.colors.purple[100] : 'transparent'};
  border-radius: 16px;
  transition:
    border 0.2s ease,
    transform 0.2s ease;

  ${isClick && ' transform: scale(0.97)'}
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
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[600]};
`;

export const panelContent = (theme: Theme, isClick: boolean) => css`
  ${theme.typography.bmHannaPro.bodyLarge};

  font-weight: 900;
  color: ${isClick ? theme.colors.purple[100] : theme.colors.black};
`;

export const captionContent = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[600]};
`;
