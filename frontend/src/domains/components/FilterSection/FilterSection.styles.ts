import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const filterSectionContainer = css`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const sortDropdownButton = css`
  border-radius: 30px;
`;

export const filterTagsContainer = css`
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 10px;
  overflow-x: auto;
  white-space: nowrap;
`;

export const tagStyle = (theme: Theme, isSelected: boolean) => css`
  color: ${isSelected ? theme.colors.white[100] : theme.colors.black[100]};
  background-color: ${isSelected
    ? theme.colors.purple[100]
    : theme.colors.white[100]};
  border: 1px solid
    ${isSelected ? theme.colors.purple[100] : theme.colors.gray[200]};
  border-radius: 20px;
  transition: all 0.2s ease;
  ${theme.typography.pretendard.captionSmall};
`;

export const tagButton = (theme: Theme) => css`
  padding: 2px;
  ${theme.typography.pretendard.captionSmall};

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
