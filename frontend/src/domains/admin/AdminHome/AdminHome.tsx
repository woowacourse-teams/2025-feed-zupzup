import { PAGE_PADDING_PX } from '@/constants';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function AdminHome() {
  const theme = useAppTheme();
  return (
    <div css={homeLayout(theme)}>
      <AdminHomeHeader />
      <div css={feedbackListContainer(theme)}>
        <div css={infoContainer}>
          <p css={listTitle(theme)}>피드백 방 목록</p>
          <p css={listCaption(theme)}>관리하고 있는 피드백 방들을 확인하세요</p>
        </div>
        <FeedbackRoom
          roomName='피드백 방 1'
          pendingCount={3}
          feedbackHoursAgo={2}
          onClick={() => console.log('피드백 방 1 클릭')}
        />
      </div>
    </div>
  );
}

export const homeLayout = (theme: Theme) => css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 36px;
  width: 100%;
  height: 100%;
  padding: 52px ${PAGE_PADDING_PX / 2}px 0 ${PAGE_PADDING_PX / 2}px;
  background-color: ${theme.colors.blue[100]};
`;

export const feedbackListContainer = (theme: Theme) => css`
  width: calc(100% + ${PAGE_PADDING_PX * 2}px);
  max-width: 1200px;
  height: 100%;
  padding: ${PAGE_PADDING_PX}px;
  background-color: ${theme.colors.white[100]};
  border-radius: 8px;
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
