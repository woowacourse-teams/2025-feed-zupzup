import {
  imgContainer,
  imgLayout,
  userNameStyle,
} from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader.style';
import useAvatarSelector from '@/domains/hooks/useAvatarSelector';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface FeedbackBoxHeaderProps {
  feedbackId: number;
  userName?: string;
  type: FeedbackStatusType;
}

export default function FeedbackBoxHeader({
  feedbackId,
  userName,
  type,
}: FeedbackBoxHeaderProps) {
  const userImage = useAvatarSelector({ feedbackId });
  const theme = useAppTheme();

  return (
    <div css={imgContainer}>
      <img src={userImage} alt='user icon' css={imgLayout} />
      <p css={userNameStyle(theme, type)}>{userName}</p>
    </div>
  );
}
