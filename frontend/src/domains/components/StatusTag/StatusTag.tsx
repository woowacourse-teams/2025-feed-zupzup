import Tag from '@/components/Tag/Tag';
import { useAppTheme } from '@/hooks/useAppTheme';
import { container, content } from './StatusTag.styles';
import Complete from '@/components/icons/Complete';

export interface StatusTagProps {
  type: StatusType;
}

export type StatusType = 'complete' | 'incomplete';

export default function StatusTag({ type = 'incomplete' }: StatusTagProps) {
  const theme = useAppTheme();

  return (
    <Tag customCss={container(theme, type)}>
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
