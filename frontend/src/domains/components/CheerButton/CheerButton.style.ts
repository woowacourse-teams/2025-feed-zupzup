import { css } from '@emotion/react';

export const cheerButtonStyle = css`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 50%;
`;

export const textStyle = css`
  font-size: 14px;
  color: #4a4a4a;
`;

export const iconWrapperStyle = css`
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const clickedStyle = css`
  animation: pop 0.3s ease;

  @keyframes pop {
    0% {
      transform: scale(1);
    }

    50% {
      transform: scale(1.3);
    }

    100% {
      transform: scale(1);
    }
  }
`;
