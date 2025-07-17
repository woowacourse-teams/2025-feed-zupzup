import { css } from '@emotion/react';
import { Theme } from '../../../../../theme';

export const basicButtonStyle = (theme: Theme, width?: string | number) => css`
  background-color: ${theme.colors.yellow[200]} !important;
  border: none;
  border-radius: 14.5px;
  padding: 12px 16px;
  width: ${typeof width === 'number' ? `${width}px` : width || 'auto'};
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.9;
  }

  &:active {
    opacity: 0.8;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const basicButtonTextStyle = (theme: Theme) => css`
  font-family: ${theme.typography.inter.small.fontFamily};
  font-weight: ${theme.typography.inter.small.fontWeight};
  font-size: ${theme.typography.inter.small.fontSize}px;
  line-height: ${theme.typography.inter.small.lineHeight}px;
  letter-spacing: ${theme.typography.inter.small.letterSpacing}px;
  color: ${theme.colors.black[100]};
  margin: 0;
`;

export const basicButtonIconStyle = css`
  display: flex;
  align-items: center;
  justify-content: center;
`;
