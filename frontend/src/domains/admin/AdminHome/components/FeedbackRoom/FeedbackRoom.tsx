import ArrowRightIcon from '@/components/icons/ArrowRightIcon';
import {
  feedbackRoomContainer,
  feedbackRoomTitle,
  feedbackTitleContainer,
  pendingText,
} from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom.style';
import { dot } from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface FeedbackRoomProps {
  roomName: string;
  pendingCount: number;
  feedbackHoursAgo: number;
}
export default function FeedbackRoom({
  roomName,
  pendingCount,
  feedbackHoursAgo,
}: FeedbackRoomProps) {
  const theme = useAppTheme();

  return (
    <div css={feedbackRoomContainer(theme)}>
      <div css={feedbackTitleContainer}>
        <div css={feedbackRoomTitle(theme)}>
          <div css={dot(theme)} />
          <span>{roomName}</span>
        </div>
        <ArrowRightIcon />
      </div>
      <p css={pendingText(theme)}>
        <strong>{pendingCount}</strong> 건 미처리
      </p>
      <p>최근 피드백: {feedbackHoursAgo}시간 전</p>
    </div>
  );
}
