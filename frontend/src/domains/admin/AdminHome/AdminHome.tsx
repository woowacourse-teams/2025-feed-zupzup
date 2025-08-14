import PlusIcon from '@/components/icons/PlusIcon';
import { PAGE_PADDING_PX } from '@/constants';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function AdminHome() {
  const theme = useAppTheme();
  return (
    <div css={homeLayout}>
      <AdminHomeHeader />
      <div css={feedbackListContainer(theme)}>
        <div css={infoContainer}>
          <p css={listTitle(theme)}>피드백 방 목록</p>
          <p css={listCaption(theme)}>관리하고 있는 피드백 방들을 확인하세요</p>
        </div>

        <div css={feedbackList}>
          <FeedbackRoom
            roomName='피드백 방 1'
            pendingCount={3}
            feedbackHoursAgo={2}
            onClick={() => console.log('피드백 방 1 클릭')}
          />
          <FeedbackRoom
            roomName='피드백 방 1'
            pendingCount={3}
            feedbackHoursAgo={2}
            onClick={() => console.log('피드백 방 1 클릭')}
          />
          {/* <FeedbackRoom
            roomName='피드백 방 1'
            pendingCount={3}
            feedbackHoursAgo={2}
            onClick={() => console.log('피드백 방 1 클릭')}
          />
          <FeedbackRoom
            roomName='피드백 방 1'
            pendingCount={3}
            feedbackHoursAgo={2}
            onClick={() => console.log('피드백 방 1 클릭')}
          /> */}
        </div>
      </div>
      <FloatingButton
        icon={<PlusIcon color='white' width='24' height='24' />}
        onClick={() => window.alert('피드백 방 추가')}
        inset={{ bottom: '80px', left: '100%' }}
        customCSS={addFeedbackRoom(theme)}
      />
    </div>
  );
}

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
