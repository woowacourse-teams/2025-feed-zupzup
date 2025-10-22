import { css } from '@emotion/react';

export const container = css`
  position: relative;
  width: 85%;
  max-width: 350px;
  aspect-ratio: 4/3;
  margin: 0 auto;
`;

export const background = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  border-radius: 24px;
  background-color: #7356ff08;
`;

export const mainBellCenter = css`
  position: absolute;
  left: 50%;
  top: 60%;
  width: 64px;
  height: 64px;
  margin-left: -32px; /* width의 절반만큼 왼쪽으로 */
  margin-top: -32px; /* height의 절반만큼 위로 */
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #7356ff;
`;

export const bellIcon = css`
  width: 32px;
  height: 32px;
  color: #ffffff;
`;

export const notificationBadge = css`
  position: absolute;
  top: 24px;
  left: 50%;
  margin-left: -12px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #7356ff;
`;

export const badgeText = css`
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
`;

export const pulseRing = css`
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 2px solid;
  pointer-events: none;
`;
