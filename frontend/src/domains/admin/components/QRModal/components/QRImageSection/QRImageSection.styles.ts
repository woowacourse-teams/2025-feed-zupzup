import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const QRTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}

  font-size: 20px;
  font-weight: 600;
`;

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
