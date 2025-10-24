import { Theme } from '@/theme';
import { css, keyframes } from '@emotion/react';

const shimmer = keyframes`
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
`;

export const container = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  min-height: 100px;
  padding: 18px;
  border-radius: 14px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);
  background-color: ${theme.colors.white};
`;

export const topContainer = css`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const iconWrap = css`
  display: flex;
  gap: 14px;
  align-items: center;
`;

export const textWrap = css`
  display: flex;
  align-items: center;
  gap: 4px;
`;

export const skeletonBase = (theme: Theme) => css`
  background: linear-gradient(
    90deg,
    ${theme.colors.gray[200]} 25%,
    ${theme.colors.gray[100]} 50%,
    ${theme.colors.gray[200]} 75%
  );
  background-size: 200px 100%;
  animation: ${shimmer} 1.5s infinite;
  border-radius: 4px;
`;

export const skeletonAvatar = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 32px;
  height: 32px;
  border-radius: 50%;
`;

export const skeletonText = (
  theme: Theme,
  size: 'short' | 'medium' | 'long'
) => css`
  ${skeletonBase(theme)};
  height: 16px;

  ${size === 'short' && 'width: 60px;'}
  ${size === 'medium' && 'width: 100px;'}
  ${size === 'long' && 'width: 150px;'}
`;

export const skeletonButton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 40px;
  height: 24px;
  border-radius: 6px;
`;

export const skeletonContent = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 100%;
  height: 20px;
  border-radius: 4px;
`;

export const skeletonFooter = css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
`;
