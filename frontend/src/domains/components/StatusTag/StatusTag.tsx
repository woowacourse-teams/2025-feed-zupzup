import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container, content } from './StatusTag.styles';
import Complete from '@/components/icons/Complete';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface StatusTagProps {
  type: FeedbackStatusType;
}

export default function StatusTag({ type = 'incomplete' }: StatusTagProps) {
  const theme = useAppTheme();

  return (
    <Tag customCSS={container(theme, type)}>
      {type === 'incomplete' ? (
        '접수'
      ) : (
        <span css={content}>
          <Complete /> 완료
        </span>
      )}
    </Tag>
  );
}
