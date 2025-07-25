import { keyframes, css } from '@emotion/react';

export const highlightFade = keyframes`
  0% {
    background-color: #f0ede6;
  }
  100% {
    background-color: white;

  }
`;

export const highlightStyle = css`
  border-radius: 12px;
  animation: ${highlightFade} 2s linear;
`;
