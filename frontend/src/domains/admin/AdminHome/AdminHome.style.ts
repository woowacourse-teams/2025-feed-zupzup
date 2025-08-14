import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const homeLayout = css`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 36px;
  width: 100%;
  max-height: calc(100vh - ${PAGE_PADDING_PX * 2}px);
  padding: 52px ${PAGE_PADDING_PX / 2}px 0 ${PAGE_PADDING_PX / 2}px;
`;

export const feedbackListContainer = (theme: Theme) => css`
  width: calc(100% + ${PAGE_PADDING_PX}px);
  min-height: calc(100% - ${PAGE_PADDING_PX}px);
  margin-top: 160px;
  background-color: ${theme.colors.white[100]};
`;

export const feedbackList = css`
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
`;

export const infoContainer = css`
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 12px;
`;

export const listTitle = (theme: Theme) => css`
  font-size: 24px;
  font-weight: 600;
  ${theme.typography.pretendard.smallBold}
`;

export const listCaption = (theme: Theme) => css`
  margin-bottom: 16px;
  font-size: 24px;
  font-weight: 600;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption}
`;

export const addFeedbackRoom = (theme: Theme) => css`
  width: 60px;
  min-height: 60px;
  background-color: ${theme.colors.purple[100]};

  &:hover {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;
