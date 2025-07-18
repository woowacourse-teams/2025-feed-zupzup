import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const categorySelector = (theme: Theme, width?: string | number) => css`
  display: inline-block;
  box-sizing: border-box;
  width: ${typeof width === 'number'
    ? `${width}px`
    : width || '100%'} !important;
  height: 32px !important;
  padding: 0 12px !important;
  font-family: ${theme.typography.inter.small.fontFamily} !important;
  font-size: ${theme.typography.inter.small.fontSize}px !important;
  font-weight: ${theme.typography.inter.small.fontWeight} !important;
  line-height: 32px !important;
  letter-spacing: ${theme.typography.inter.small.letterSpacing} !important;
  color: ${theme.colors.darkGray[400]} !important;
  background: ${theme.colors.white[100]} !important;
  background-color: ${theme.colors.white[100]} !important;
  border: 1px solid ${theme.colors.gray[200]} !important;
  border-width: 1px !important;
  border-style: solid !important;
  border-color: ${theme.colors.gray[200]} !important;
  border-radius: 14px !important;
  cursor: pointer !important;
  transition: border-color 0.2s ease !important;
  all: unset;
  outline: none !important;

  &:hover {
    border-color: ${theme.colors.gray[300]} !important;
  }

  &:disabled {
    opacity: 0.5 !important;
    cursor: not-allowed !important;
  }

  option {
    padding: 8px 12px !important;
    font-family: ${theme.typography.inter.small.fontFamily} !important;
    font-size: ${theme.typography.inter.small.fontSize}px !important;
    color: ${theme.colors.darkGray[400]} !important;
    background-color: ${theme.colors.white[100]} !important;
  }

  option:disabled {
    color: ${theme.colors.gray[400]} !important;
  }
`;
