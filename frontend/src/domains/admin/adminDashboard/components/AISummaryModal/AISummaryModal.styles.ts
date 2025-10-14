import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const aiSummaryContainer = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
`;

export const aiSummaryTitle = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.bodyBold};
  color: ${theme.colors.black[100]};
  text-align: center;
  margin-bottom: 8px;
`;

export const aiSummaryDescription = (theme: Theme) => css`
  ${theme.typography.BMHANNAAir.caption};
  color: ${theme.colors.gray[600]};
  text-align: center;
  line-height: 1.5;
  margin-bottom: 16px;
`;

export const categoriesContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  width: 100%;
  border: 1px solid ${theme.colors.purple[100]};
  border-radius: 16px;
  padding: 8px 16px;
  background-color: ${theme.colors.white[400]};
`;

export const categoryItem = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;

  border-bottom: 1px solid ${theme.colors.gray[200]};
  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: ${theme.colors.gray[100]};
  }
`;

export const categoryText = (theme: Theme) => css`
  ${theme.typography.BMHANNAAir.caption};
  color: ${theme.colors.black[100]};
  flex: 1;
`;

export const arrowIcon = (theme: Theme) => css`
  ${theme.typography.BMHANNAAir.bodyBold};
  color: ${theme.colors.gray[600]};
  margin-left: 8px;
`;

export const footerText = (theme: Theme) => css`
  ${theme.typography.BMHANNAAir.caption};
  color: ${theme.colors.purple[100]};
  text-align: center;
`;

export const closeButton = (theme: Theme) => css`
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 50%;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: ${theme.colors.gray[100]};
  }
`;

export const modalWidth = css`
  width: 80%;
  min-width: 300px;
  max-width: 450px;
  position: relative;
`;
