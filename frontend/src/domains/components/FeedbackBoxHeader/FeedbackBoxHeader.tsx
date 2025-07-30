import {
  imgContainer,
  imgLayout,
  userNameStyle,
} from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader.style';
import useRandomAvatar from '@/domains/hooks/useRandomAvata';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface FeedbackBoxHeaderProps {
  userName?: string;
  type: FeedbackStatusType;
}

export default function FeedbackBoxHeader({
  userName,
  type,
}: FeedbackBoxHeaderProps) {
  const userImage = useRandomAvatar();
  const theme = useAppTheme();

  return (
    <div css={imgContainer}>
      <img src={userImage} alt='user icon' css={imgLayout} />
      <p css={userNameStyle(theme, type)}>{userName}</p>
    </div>
  );
}
