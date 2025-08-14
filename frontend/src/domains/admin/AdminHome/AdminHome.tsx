import PlusIcon from '@/components/icons/PlusIcon';
import {
  addFeedbackRoom,
  feedbackList,
  feedbackListContainer,
  homeLayout,
  infoContainer,
  listCaption,
  listTitle,
} from '@/domains/admin/AdminHome/AdminHome.style';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import { useAppTheme } from '@/hooks/useAppTheme';

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
