import { css } from '@emotion/react';
import { Theme } from '../../theme';

export const basicButtonStyle = (
  theme: Theme,
  width?: string | number,
  variant: 'primary' | 'secondary' = 'primary'
) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  width: ${typeof width === 'number' ? `${width}px` : width || 'auto'};
  height: 42px;
  padding: 12px 16px;
  background-color: ${variant === 'primary'
    ? theme.colors.yellow[200]
    : theme.colors.white[100]} !important;
  border: ${variant === 'secondary'
    ? `1px solid ${theme.colors.darkGray[100]}`
    : 'none'};
  border-radius: 14.5px;
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

export const basicButtonTextStyle = (
  theme: Theme,
  variant: 'primary' | 'secondary' = 'primary'
) => css`
  margin: 0;
  font-family: ${theme.typography.inter.small.fontFamily};
  font-size: ${theme.typography.inter.small.fontSize}px;
  font-weight: ${theme.typography.inter.small.fontWeight};
  line-height: ${theme.typography.inter.small.lineHeight}px;
  letter-spacing: ${theme.typography.inter.small.letterSpacing}px;
  color: ${variant === 'primary'
    ? theme.colors.black[100]
    : theme.colors.darkGray[100]};
`;

export const basicButtonIconStyle = css`
  display: flex;
  justify-content: center;
  align-items: center;
`;
