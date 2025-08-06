import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const dropdownContainer = (width?: string, height?: string) => css`
  position: relative;
  display: inline-block;
  width: ${width || 'auto'};
  height: ${height || 'auto'};
`;

export const dropdownButton = (theme: Theme, isOpen: boolean) => css`
  ${theme.typography.pretendard.caption};

  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 60px;
  padding: 0 12px;
  color: ${theme.colors.black[100]};
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
    ${theme.typography.pretendard.caption}
  }

  svg {
    flex-shrink: 0;
    transition: transform 0.2s ease;
    transform: ${isOpen ? 'rotate(180deg)' : 'rotate(0deg)'};
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
  right: 0;
  left: 0;
  z-index: 10;
  max-height: 200px;
  margin-top: 4px;
  background-color: ${theme.colors.white[100]};
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 8px;
  box-shadow: 0 4px 12px rgb(0 0 0 / 10%);
  overflow-y: auto;
`;

export const dropdownItem = (theme: Theme, isDisabled: boolean) => css`
  padding: 8px 12px;
  color: ${isDisabled ? theme.colors.gray[400] : theme.colors.darkGray[400]};
  opacity: ${isDisabled ? 0.5 : 1};
  cursor: ${isDisabled ? 'not-allowed' : 'pointer'};
  transition: background-color 0.2s ease;

  ${theme.typography.pretendard.caption}

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
