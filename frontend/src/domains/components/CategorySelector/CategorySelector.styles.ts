import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const categorySelector = (theme: Theme, width?: string | number) => css`
  all: unset;
  box-sizing: border-box;

  display: inline-block;
  width: ${typeof width === 'number'
    ? `${width}px`
    : width || '100%'} !important;
  height: 32px !important;
  padding: 0 12px !important;
  line-height: 32px !important;

  background-color: ${theme.colors.white[100]} !important;
  background: ${theme.colors.white[100]} !important;
  border: 1px solid ${theme.colors.gray[200]} !important;
  border-width: 1px !important;
  border-style: solid !important;
  border-color: ${theme.colors.gray[200]} !important;
  border-radius: 14.5px !important;

  font-family: ${theme.typography.inter.small.fontFamily} !important;
  font-size: ${theme.typography.inter.small.fontSize}px !important;
  font-weight: ${theme.typography.inter.small.fontWeight} !important;
  letter-spacing: ${theme.typography.inter.small.letterSpacing} !important;
  color: ${theme.colors.darkGray[400]} !important;

  cursor: pointer !important;
  outline: none !important;
  transition: border-color 0.2s ease !important;

  &:hover {
    border-color: ${theme.colors.gray[300]} !important;
  }

  &:disabled {
    opacity: 0.5 !important;
    cursor: not-allowed !important;
  }

  option {
    color: ${theme.colors.darkGray[400]} !important;
    background-color: ${theme.colors.white[100]} !important;
    padding: 8px 12px !important;
    font-family: ${theme.typography.inter.small.fontFamily} !important;
    font-size: ${theme.typography.inter.small.fontSize}px !important;
  }

  option:disabled {
    color: ${theme.colors.gray[400]} !important;
  }
`;
