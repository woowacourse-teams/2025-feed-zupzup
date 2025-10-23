import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const headerLayout = (
  backgroundPng: string,
  backgroundWebp: string
) => css`
  position: fixed;
  top: 0;
  left: 50%;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 600px;
  padding: 20px 30px;
  background-image: url(${backgroundPng});
  background-image: image-set(
    url(${backgroundWebp}) type('image/webp'),
    url(${backgroundPng}) type('image/png')
  );
  background-size: cover;
  background-position: center;
  transform: translateX(-50%);
`;

export const logoContainer = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const basketIcon = css`
  width: 24px;
  height: 24px;
`;

export const logoText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionBold};
  color: ${theme.colors.white[100]};
  letter-spacing: 0.1em;
`;
