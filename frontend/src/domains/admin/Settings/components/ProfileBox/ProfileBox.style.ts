import { css } from '@emotion/react';
import { Theme } from '@/theme';
export const profileBox = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 20px 24px;
  background-color: ${theme.colors.purple[100]}22;
  border-radius: 10px;
`;

export const adminName = (theme: Theme) => css`
  ${theme.typography.pretendard.captionBold}

  color: ${theme.colors.purple[200]};
`;

export const adminId = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  color: ${theme.colors.gray[600]};
`;
