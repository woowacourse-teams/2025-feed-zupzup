import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';
import { feedbackList } from '@/domains/admin/AdminHome/components/FeedbackRoomList/FeedbackRoomList.style';
import { feedbackRooms } from '@/domains/admin/AdminHome/mocks/feedbackRooms';

export default function FeedbackRoomList() {
  return (
    <div css={feedbackList}>
      {feedbackRooms.map((room, idx) => (
        <FeedbackRoom
          key={idx}
          roomName={room.roomName}
          pendingCount={room.pendingCount}
          feedbackHoursAgo={room.feedbackHoursAgo}
          onClick={room.onClick}
        />
      ))}
    </div>
  );
}
