import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const uploadBox = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 30px;
  border: 4px dotted ${theme.colors.gray[200]};
  border-radius: 16px;
  cursor: pointer;

  :hover {
    background-color: whitesmoke;
  }
`;

export const uploadText = (theme: Theme) => css`
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption}
`;

export const uploadButton = (theme: Theme) => css`
  padding: 8px 16px;
  font-weight: 800;
  color: ${theme.colors.yellow[200]};
  background-color: white;
  border: 1px solid ${theme.colors.yellow[200]};
  border-radius: 50%;

  ${theme.typography.pretendard.caption};
`;

export const previewImage = css`
  width: 100%;
  height: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  object-fit: cover;
`;
