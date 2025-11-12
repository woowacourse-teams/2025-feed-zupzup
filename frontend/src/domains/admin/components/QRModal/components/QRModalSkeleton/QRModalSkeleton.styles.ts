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

export const container = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: center;
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

export const titleSkeleton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 200px;
  height: 24px;
  margin: 0 auto;
`;

export const qrImageSkeleton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 200px;
  height: 200px;
  border-radius: 8px;
  margin: 0 auto;
`;

export const textSkeleton = (
  theme: Theme,
  size: 'short' | 'medium' | 'long'
) => css`
  ${skeletonBase(theme)};
  height: 16px;
  margin: 0 auto;

  ${size === 'short' && 'width: 100px;'}
  ${size === 'medium' && 'width: 250px;'}
  ${size === 'long' && 'width: 300px;'}
`;

export const buttonSkeleton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  width: 140px;
  height: 30px;
  border-radius: 6px;
  margin: 0 auto;
`;

export const urlContainer = css`
  display: flex;
  justify-content: space-between;
  width: 100%;
  gap: 10px;
`;

export const urlBoxSkeleton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  flex: 0 0 73%;
  height: 30px;
  border-radius: 8px;
`;

export const copyButtonSkeleton = (theme: Theme) => css`
  ${skeletonBase(theme)};
  flex: 0 0 25%;
  height: 30px;
  border-radius: 6px;
`;
