import { Theme } from '@/theme';
import { css, keyframes } from '@emotion/react';

export const feedbackText = (
  theme: Theme,
  type: 'CONFIRMED' | 'WAITING'
) => css`
  ${theme.typography.pretendard.caption}

  line-height: 24px;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-wrap: break-word;

  ${type === 'WAITING'
    ? `color : ${theme.colors.darkGray[100]}`
    : `color : ${theme.colors.gray[300]}`}
`;

export const feedbackTextContainer = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const slideDown = keyframes`
  from {
    opacity: 0;
    transform: translateY(-6px);
    max-height: 0;
  }
  to {
    opacity: 1;
    transform: translateY(0);
    max-height: 350px; 
  }
`;

const slideUp = keyframes`
  from {
    opacity: 1;
    transform: translateY(0);
    max-height: 350px;
  }
  to {
    opacity: 0;
    transform: translateY(-6px);
    max-height: 0;
  }
`;

export const feedbackImageButton = (theme: Theme, showImg: boolean) => css`
  display: flex;
  align-items: center;
  gap: 8px;
  color: ${theme.colors.gray[400]};

  ${theme.typography.pretendard.captionSmall}

  transition: color 0.2s ease, transform 0.2s ease;

  svg {
    transition: transform 0.25s ease;
    transform: rotate(${showImg ? '0deg' : '90deg'});
  }
`;

export const feedbackImage = (isVisible: boolean) => css`
  width: 100%;
  border-radius: 8px;
  object-fit: contain;
  overflow: hidden;
  animation: ${isVisible ? slideDown : slideUp} 0.35s ease forwards;
  will-change: transform, opacity;
`;
