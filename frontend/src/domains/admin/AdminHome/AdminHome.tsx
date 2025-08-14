import PlusIcon from '@/components/icons/PlusIcon';
import {
  addFeedbackRoom,
  feedbackListContainer,
  homeLayout,
  infoContainer,
  listCaption,
  listTitle,
} from '@/domains/admin/AdminHome/AdminHome.style';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import FeedbackRoomList from '@/domains/admin/AdminHome/components/FeedbackRoomList/FeedbackRoomList';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import { useAppTheme } from '@/hooks/useAppTheme';

const ADMIN = '관리자1';
const COMPLETED_COUNT = 17;
const TOTAL_COUNT = 29;

export default function AdminHome() {
  const theme = useAppTheme();
  return (
    <div css={homeLayout}>
      <AdminHomeHeader
        adminName={ADMIN}
        completedCount={COMPLETED_COUNT}
        totalCount={TOTAL_COUNT}
      />
      <div css={feedbackListContainer(theme)}>
        <div css={infoContainer}>
          <p css={listTitle(theme)}>피드백 방 목록</p>
          <p css={listCaption(theme)}>관리하고 있는 피드백 방들을 확인하세요</p>
        </div>

        <FeedbackRoomList />
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
