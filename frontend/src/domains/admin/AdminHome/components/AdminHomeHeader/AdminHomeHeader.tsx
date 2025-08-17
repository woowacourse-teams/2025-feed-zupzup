import Tag from '@/components/Tag/Tag';
import {
  feedbackProgressStatus,
  headerLayout,
  homeCaptionContainer,
  homeTag,
  homeTitle,
  progressStatus,
  progressStatusSummary,
} from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader.style';
import StatusBar from '@/domains/admin/AdminHome/components/StatusBar/StatusBar';
import { useAppTheme } from '@/hooks/useAppTheme';

interface AdminHomeHeaderProps {
  adminName: string;
  completedCount: number;
  totalCount: number;
}

export default function AdminHomeHeader({
  adminName,
  completedCount,
  totalCount,
}: AdminHomeHeaderProps) {
  const theme = useAppTheme();

  const completeStatus = Number(
    ((completedCount / totalCount) * 100).toFixed(1)
  );
  const remainingCount = totalCount - completedCount;

  return (
    <div css={headerLayout(theme)}>
      <div css={homeCaptionContainer(theme)}>
        <p css={homeTitle(theme)}>피드줍줍</p>
        <Tag customCSS={homeTag(theme)}>{adminName}</Tag>
      </div>

      <div css={feedbackProgressStatus(theme)}>
        <div css={progressStatus(theme)}>
          <p>전체 처리 현황</p>
          <p>
            {completedCount} / {totalCount}
          </p>
        </div>
        <StatusBar status={completeStatus} />
        <p css={progressStatusSummary(theme)}>
          {completeStatus}% 완료 - {remainingCount}개 남음
        </p>
      </div>
    </div>
  );
}
