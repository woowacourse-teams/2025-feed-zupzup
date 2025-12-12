import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const editRoomModalContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

export const editRoomModalTitle = css`
  text-align: center;
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  gap: 10px;

  ${theme.typography.pretendard.caption}
`;

export const tabContainer = css`
  display: flex;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 16px;
`;

export const tabButton = (isActive: boolean) => css`
  flex: 1;
  padding: 12px 16px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  font-weight: ${isActive ? 600 : 400};
  color: ${isActive ? '#222' : '#888'};
  border-bottom: 2px solid ${isActive ? '#222' : 'transparent'};
  transition: all 0.2s ease;
`;

export const deleteContent = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px 0;
  text-align: center;
`;

export const deleteMessage = css`
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-line;
`;
