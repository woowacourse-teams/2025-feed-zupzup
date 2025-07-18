import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const dropdownContainer = (theme: Theme, width?: string | number) => css`
  position: relative;
  display: inline-block;
  width: ${typeof width === 'number' ? `${width}px` : width || 'auto'};
`;

export const dropdownButton = (theme: Theme, isOpen: boolean) => css`
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 32px;
  padding: 0 12px;
  font-family: ${theme.typography.inter.small.fontFamily};
  font-size: ${theme.typography.inter.small.fontSize}px;
  font-weight: ${theme.typography.inter.small.fontWeight};
  color: ${theme.colors.darkGray[400]};
  background-color: ${theme.colors.white[100]};
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 14px;
  cursor: pointer;
  transition: border-color 0.2s ease;
  outline: none;

  &:hover {
    border-color: ${theme.colors.gray[300]};
  }

  &:focus {
    border-color: ${theme.colors.gray[300]};
    box-shadow: 0 0 0 2px ${theme.colors.gray[100]};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;

    &:hover {
      border-color: ${theme.colors.gray[200]};
    }
  }

  svg {
    flex-shrink: 0;
    transform: ${isOpen ? 'rotate(180deg)' : 'rotate(0deg)'};
    transition: transform 0.2s ease;
  }
`;

export const dropdownText = (theme: Theme, hasSelectedOption: boolean) => css`
  color: ${hasSelectedOption
    ? theme.colors.darkGray[400]
    : theme.colors.gray[400]};
`;

export const dropdownList = (theme: Theme) => css`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background-color: ${theme.colors.white[100]};
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  max-height: 200px;
  overflow-y: auto;
`;

export const dropdownItem = (
  theme: Theme,
  isSelected: boolean,
  isDisabled: boolean
) => css`
  padding: 8px 12px;
  font-family: ${theme.typography.inter.small.fontFamily};
  font-size: ${theme.typography.inter.small.fontSize}px;
  color: ${isDisabled ? theme.colors.gray[400] : theme.colors.darkGray[400]};
  background-color: ${isSelected ? theme.colors.gray[100] : 'transparent'};
  cursor: ${isDisabled ? 'not-allowed' : 'pointer'};
  transition: background-color 0.2s ease;
  opacity: ${isDisabled ? 0.5 : 1};

  &:hover {
    background-color: ${isDisabled ? 'transparent' : theme.colors.gray[100]};
  }

  &:first-of-type {
    border-radius: 8px 8px 0 0;
  }

  &:last-of-type {
    border-radius: 0 0 8px 8px;
  }

  &:only-child {
    border-radius: 8px;
  }
`;
