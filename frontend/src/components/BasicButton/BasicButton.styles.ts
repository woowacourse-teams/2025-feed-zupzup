import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const basicButton = (
  theme: Theme,
  width?: string | number,
  variant: 'primary' | 'secondary' | 'disabled' = 'primary',
  height?: string | number,
  gap?: string | number,
  padding?: string | number
) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: ${gap || '14px'};
  width: ${width || '100%'};
  height: ${height ?? '54px'};
  padding: ${padding || '24px 28px'};
  background-color: ${variant === 'primary'
    ? theme.colors.purple[100]
    : variant === 'disabled'
      ? theme.colors.gray[100]
      : theme.colors.white[100]} !important;
  border: ${variant === 'secondary'
    ? `1px solid ${theme.colors.gray[300]}`
    : 'none'};
  border-radius: 24px;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    ${variant === 'primary'
      ? 'opacity: 0.9;'
      : `
        background-color: ${theme.colors.white[300]};
        border-color: ${theme.colors.gray[200]};
      `}
  }

  &:active {
    ${variant === 'primary'
      ? 'opacity: 0.8;'
      : `
        background-color: ${theme.colors.white[200]};
        border-color: ${theme.colors.gray[200]};
      `}
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const basicButtonText = (
  theme: Theme,
  variant: 'primary' | 'secondary' | 'disabled' = 'primary',
  fontSize?: string
) => css`
  ${theme.typography.pretendard.small};

  margin: 0;
  font-size: ${fontSize || '16px'};
  color: ${variant === 'primary'
    ? theme.colors.white[100]
    : variant === 'disabled'
      ? theme.colors.gray[500]
      : 'black'};
`;

export const basicButtonIcon = css`
  display: flex;
  justify-content: center;
  align-items: center;
`;
