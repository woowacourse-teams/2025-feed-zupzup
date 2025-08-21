import { CategoryListType } from '@/constants/categoryList';
import {
  headerInfoBox,
  imgContainer,
  imgLayout,
  userCategoryStyle,
  userNameStyle,
} from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader.style';
import useAvatarSelector from '@/domains/hooks/useAvatarSelector';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface FeedbackBoxHeaderProps {
  feedbackId: number;
  userName?: string;
  type: FeedbackStatusType;
  category: CategoryListType;
}

export default function FeedbackBoxHeader({
  feedbackId,
  userName,
  type,
  category,
}: FeedbackBoxHeaderProps) {
  const userImage = useAvatarSelector({ feedbackId });
  const theme = useAppTheme();

  return (
    <div css={imgContainer}>
      <img src={userImage} alt='user icon' css={imgLayout} />
      <div css={headerInfoBox}>
        <p css={userNameStyle(theme, type)}>{userName}</p>
        <p css={userCategoryStyle(theme, type)}>{category}</p>
      </div>
    </div>
  );
}
