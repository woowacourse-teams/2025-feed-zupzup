import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const headerLayout = css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 24px;
  width: 100%;
  padding: 52px 30px 0px 30px;
  background-color: transparent;

  @media (max-height: 700px) {
    padding: 52px 24px 0px 24px;
    gap: 16px;
  }

  @media (max-height: 600px) {
    padding: 52px 20px 0px 20px;
    gap: 12px;
  }
`;

export const homeCaptionContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption};

  strong {
    color: ${theme.colors.purple[100]};
    cursor: pointer;
  }
`;

export const homeTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};

  color: ${theme.colors.white[100]};
`;

export const logoContainer = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const basketIcon = css`
  width: 24px;
  height: 24px;

  @media (max-height: 700px) {
    width: 20px;
    height: 20px;
  }

  @media (max-height: 600px) {
    width: 18px;
    height: 18px;
  }
`;

export const logoText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionBold};
  color: ${theme.colors.white[100]};
  letter-spacing: 0.1em;

  @media (max-height: 700px) {
    font-size: 12px;
  }

  @media (max-height: 600px) {
    font-size: 11px;
  }
`;

export const greetingContainer = css`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  margin-top: 50px;

  @media (max-height: 700px) {
    margin-top: 32px;
    gap: 4px;
  }

  @media (max-height: 600px) {
    margin-top: 24px;
    gap: 2px;
  }
`;

export const greetingBold = (theme: Theme) => css`
  font-size: 30px;
  font-weight: 700;
  color: ${theme.colors.white[100]};

  @media (max-height: 700px) {
    font-size: 24px;
  }

  @media (max-height: 600px) {
    font-size: 20px;
  }
`;

export const greetingLight = (theme: Theme) => css`
  font-size: 30px;
  font-weight: 300;
  color: ${theme.colors.white[100]};

  @media (max-height: 700px) {
    font-size: 24px;
  }

  @media (max-height: 600px) {
    font-size: 20px;
  }
`;

export const homeTag = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall};

  padding: 6px 12px;
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.white[100]}33;
`;

export const feedbackProgressStatus = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 20px;
  color: ${theme.colors.gray[500]};
  background-color: ${theme.colors.white[100]}22;
  border-radius: 12px;

  ${theme.typography.pretendard.captionSmall};
`;

export const progressBar = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 8px;
  background-color: ${theme.colors.gray[200]};
  border-radius: 4px;
  overflow: hidden;
`;

export const progressStatus = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[200]};

  > p:first-of-type {
    ${theme.typography.pretendard.captionBold};
  }
`;

export const progressStatusSummary = (theme: Theme) => css`
  display: flex;
  justify-content: start;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[300]};
  border-radius: 4px;

  ${theme.typography.pretendard.captionSmall};
`;
