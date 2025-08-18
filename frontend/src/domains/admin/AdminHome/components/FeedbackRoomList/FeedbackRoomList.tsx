import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';
import { feedbackList } from '@/domains/admin/AdminHome/components/FeedbackRoomList/FeedbackRoomList.style';
import { feedbackRooms } from '@/domains/admin/AdminHome/mocks/feedbackRooms';
import { useNavigate } from 'react-router-dom';

export default function FeedbackRoomList() {
  const navigate = useNavigate();
  return (
    <div css={feedbackList}>
      {feedbackRooms.map((room, idx) => (
        <FeedbackRoom
          key={idx}
          roomName={room.roomName}
          pendingCount={room.pendingCount}
          feedbackHoursAgo={room.feedbackHoursAgo}
          onClick={() => navigate(`/admin/${room.roomId}/dashboard`)}
        />
      ))}
    </div>
  );
}
