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
  border-radius: 10px;
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
  width: 100%;
  border-radius: 10px;
`;

export const previewImage = css`
  display: block;
  max-height: 120px;
  border-radius: 10px;
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
