import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const modalTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  font-size: 20px;
  font-weight: 600;
  text-align: center;
`;

export const tabContainer = (theme: Theme) => css`
  display: flex;
  border-bottom: 4px solid ${theme.colors.gray[200]};
  margin-bottom: 16px;
  margin: 0 -24px;
`;

export const tabButton = (theme: Theme, isActive: boolean) => css`
  flex: 1;
  padding: 8px 0;
  border: none;
  background: transparent;
  ${theme.typography.pretendard.caption}
  cursor: pointer;
  color: ${isActive ? theme.colors.purple[100] : theme.colors.gray[400]};
  border-bottom: 4px solid
    ${isActive ? theme.colors.purple[100] : 'transparent'};
  margin-bottom: -4px;
  transition: all 0.2s ease;
  text-align: center;
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  gap: 10px;

  ${theme.typography.pretendard.caption}
`;
