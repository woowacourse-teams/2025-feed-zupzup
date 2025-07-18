import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const uploadBox = (theme: Theme) => css`
  width: 100%;
  border: 4px dotted ${theme.colors.gray[200]};
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
  align-items: center;
  padding: 30px;
  cursor: pointer;

  :hover {
    background-color: whitesmoke;
  }
`;

export const uploadText = (theme: Theme) => css`
  color: ${theme.colors.gray[500]};
  ${theme.typography.inter.bodyRegular}
`;

export const uploadButton = (theme: Theme) => css`
  color: white;
  background-color: ${theme.colors.yellow[200]};

  border: 1px solid ${theme.colors.yellow[200]};
  border-radius: 9999px;
  ${theme.typography.inter.small}
  padding: 8px 16px;
`;

export const previewImage = css`
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
`;
