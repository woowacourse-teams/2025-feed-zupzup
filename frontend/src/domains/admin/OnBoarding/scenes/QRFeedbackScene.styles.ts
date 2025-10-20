import { css } from '@emotion/react';

export const container = css`
  position: relative;
  width: 85%;
  max-width: 350px;
  aspect-ratio: 4/3;
  margin: 0 auto;
`;

export const backgroundCard = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  border-radius: 24px;
  overflow: hidden;
  background-color: #7356ff08;
`;

export const headerSection = css`
  position: absolute;
  top: 16px;
  left: 16px;
  right: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
`;

export const headerIcon = css`
  width: 24px;
  height: 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #7356ff;
`;

export const headerIconDot = css`
  width: 8px;
  height: 8px;
  background-color: #ffffff;
  border-radius: 50%;
`;

export const mainContent = css`
  position: absolute;
  left: 24px;
  right: 24px;
  top: 56px;
  bottom: 24px;
  display: flex;
  gap: 12px;
`;

export const qrCard = css`
  flex: 1;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  position: relative;
  background-color: #7356ff;
`;

export const urlCard = css`
  flex: 1;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background-color: #7356ff70;
`;

export const iconWrapper = css`
  width: 32px;
  height: 32px;
  color: #ffffff;
`;

export const scanEffect = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  border-radius: 16px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.3) 50%,
    transparent 100%
  );
`;
