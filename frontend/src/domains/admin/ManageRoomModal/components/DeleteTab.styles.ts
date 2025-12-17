import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const deleteContent = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  align-items: flex-start;
`;

export const warningText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  line-height: 1.5;
`;

export const roomInfoBox = (theme: Theme) => css`
  width: 100%;
  padding: 12px 16px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 8px;
`;

export const roomInfoLabel = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[500]};
  margin-bottom: 4px;
`;

export const roomInfoName = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  font-weight: 600;
  color: ${theme.colors.black[100]};
`;

export const deleteItemsTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  margin-bottom: 8px;
`;

export const deleteItemsList = (theme: Theme) => css`
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  line-height: 1.8;
`;

export const deleteAgreementLabel = (theme: Theme) => css`
  display: flex;
  align-items: flex-start;
  gap: 10px;
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.black[100]};
  cursor: pointer;
  line-height: 1.5;
`;

export const deleteCheckbox = css`
  margin-top: 2px;
  cursor: pointer;
`;
