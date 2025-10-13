import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 60%;
`;

export const controlRow = css`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  width: 100%;
`;

export const uploadLabel = (theme: Theme) => css`
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  color: ${theme.colors.gray[100]};
  background: ${theme.colors.gray[500]};
  border-radius: 24px;
  cursor: pointer;
  ${theme.typography.pretendard.caption};

  &:hover {
    background: ${theme.colors.gray[400]};
  }
`;

export const fileInput = css`
  display: none;
`;

export const previewWrap = css`
  position: relative;
  height: 100px;
  border-radius: 10px;
`;

export const previewImage = css`
  display: block;
  height: 100%;
`;

export const fileInfo = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall};

  width: 50%;
  text-align: left;
  white-space: nowrap;
  color: ${theme.colors.gray[400]};
  overflow: hidden;
  text-overflow: ellipsis;
`;

export const cancelButton = css`
  position: absolute;
  top: 8px;
  right: 8px;

  color: white;
  border: none;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
`;

export const cancelIcon = css`
  width: 16px;
  height: 16px;
`;
