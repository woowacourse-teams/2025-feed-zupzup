import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const settingsContainer = css`
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

export const withdrawModalContent = css`
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  align-items: flex-start;
`;

export const withdrawWarningText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  line-height: 1.5;
`;

export const withdrawDeletedItemsList = (theme: Theme) => css`
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  line-height: 1.8;
`;

export const withdrawAgreementLabel = (theme: Theme) => css`
  display: flex;
  align-items: flex-start;
  gap: 8px;
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.black[100]};
  cursor: pointer;
  line-height: 1.5;
`;

export const withdrawCheckbox = css`
  margin-top: 2px;
  cursor: pointer;
`;

export const withdrawSettingListBox = css`
  margin-top: 30px;
`;
