import { css } from '@emotion/react';
import { Theme } from '@/theme';
import { SettingListBoxVariant } from './SettingListBox';

export const container = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: ${theme.colors.white[100]};
  border-radius: 12px;
  box-shadow: 0 1px 3px rgb(0 0 0 / 10%);
`;

export const leftSection = css`
  display: flex;
  flex: 1;
  align-items: center;
`;

export const iconContainer = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  width: 35px;
  height: 35px;
  margin-right: 10px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 50%;
`;

export const textContainer = css`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
`;

export const listTitle = (theme: Theme, variant?: SettingListBoxVariant) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${variant === 'danger'
    ? theme.colors.red[200]
    : theme.colors.darkGray[100]};
`;

export const listDescription = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[500]};
`;

export const rightSection = css`
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 16px;
`;
