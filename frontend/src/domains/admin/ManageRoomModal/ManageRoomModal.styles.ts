import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const modalTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  font-size: 20px;
  font-weight: 600;
  text-align: center;
`;

export const tabContainer = css`
  display: flex;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 16px;
`;

export const tabButton = (isActive: boolean) => css`
  flex: 1;
  padding: 12px 16px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  font-weight: ${isActive ? 600 : 400};
  color: ${isActive ? '#222' : '#888'};
  border-bottom: 2px solid ${isActive ? '#222' : 'transparent'};
  transition: all 0.2s ease;
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  gap: 10px;

  ${theme.typography.pretendard.caption}
`;

// EditTab styles
export const editTabContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

// DeleteTab styles
export const deleteContent = css`
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  align-items: flex-start;
`;

export const deleteWarningText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
  line-height: 1.5;
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
  gap: 8px;
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.black[100]};
  cursor: pointer;
  line-height: 1.5;
`;

export const deleteCheckbox = css`
  margin-top: 2px;
  cursor: pointer;
`;
