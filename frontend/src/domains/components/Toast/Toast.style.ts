import { css, keyframes } from '@emotion/react';

// Toast 등장 애니메이션
const slideInFromTop = keyframes`
  from {
    transform: translateX(-50%) translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(-50%) translateY(0);
    opacity: 1;
  }
`;

// Toast 사라지는 애니메이션
const slideOutToTop = keyframes`
  from {
    transform: translateX(-50%) translateY(0);
    opacity: 1;
  }
  to {
    transform: translateX(-50%) translateY(-100%);
    opacity: 0;
  }
`;

export const toastStyle = (isExiting?: boolean) => css`
  position: fixed;
  top: 50px;
  left: 50%;
  z-index: 1050;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  font-size: 14px;
  font-weight: 400;
  color: #000;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateX(-50%);
  max-width: 400px;
  animation: ${isExiting ? slideOutToTop : slideInFromTop} 0.3s ease-out
    ${isExiting ? 'forwards' : 'none'};
`;
