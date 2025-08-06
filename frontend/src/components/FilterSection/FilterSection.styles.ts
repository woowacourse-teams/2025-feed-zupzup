import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const filterSectionContainer = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 20px;
  background-color: ${theme.colors.white[100]};
`;

export const sortDropdownButton = css`
  border-radius: 30px;
`;

export const filterTagsContainer = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const tagStyle = (theme: Theme, isSelected: boolean) => css`
  background-color: ${isSelected
    ? theme.colors.purple[100]
    : theme.colors.gray[100]};
  border: 1px solid
    ${isSelected ? theme.colors.purple[100] : theme.colors.gray[200]};
  border-radius: 20px;
  transition: all 0.2s ease;

  &:hover {
    background-color: ${isSelected
      ? theme.colors.purple[100]
      : theme.colors.gray[200]};
  }
`;

export const tagButton = css`
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: inherit;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;

  &:focus {
    outline: none;
  }
`;

export const sortDropdownContainer = css`
  flex-shrink: 0;
`;
