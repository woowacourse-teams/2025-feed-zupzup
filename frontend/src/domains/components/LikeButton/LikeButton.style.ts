import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const likeButton = css`
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: monospace;
`;

export const iconWrapper = (theme: Theme, isLiked: boolean) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  color: ${isLiked ? theme.colors.red[50] : theme.colors.gray[300]};
`;

export const adminLikeText = (theme: Theme) => css`
  color: ${theme.colors.gray[600]};
  ${theme.typography.pretendard.captionSmall}
`;

export const likeCountText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  font-weight: 800;
  letter-spacing: 1px;
  color: ${theme.colors.gray[600]};
`;
