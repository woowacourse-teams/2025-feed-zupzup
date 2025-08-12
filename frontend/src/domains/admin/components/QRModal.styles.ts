import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const QRCodeContainer = css`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
  text-align: center;
`;

export const QRImageContainer = css`
  display: flex;
  justify-content: center;
`;

export const QRText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  font-size: 12px;
  color: ${theme.colors.darkGray[100]};
`;

export const QRTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  font-size: 20px;
  font-weight: 600;
`;

export const QRURLContainer = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const URLContainer = css`
  display: flex;
  justify-content: space-between;
  width: 100%;
`;

export const URLBox = (theme: Theme) => css`
  display: flex;
  flex: 0 0 73%;
  align-items: center;
  padding: 8px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 8px;
  overflow: auto;
`;

export const copyButton = css`
  flex: 0 0 25%;
`;
