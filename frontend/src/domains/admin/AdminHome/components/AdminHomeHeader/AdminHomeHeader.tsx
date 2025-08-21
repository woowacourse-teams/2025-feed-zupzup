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
import useAdminStatistics from '@/domains/admin/AdminHome/hooks/useAdminStatistics';
import { useAppTheme } from '@/hooks/useAppTheme';

interface AdminHomeHeaderProps {
  adminName: string;
}

export default function AdminHomeHeader({ adminName }: AdminHomeHeaderProps) {
  const theme = useAppTheme();

  const { statistics, isStatisticsLoading } = useAdminStatistics();
  const remainingCount = statistics.totalCount - statistics.confirmedCount;

  return (
    <div css={headerLayout(theme)}>
      <div css={homeCaptionContainer(theme)}>
        <p css={homeTitle(theme)}>피드줍줍</p>
        <Tag customCSS={homeTag(theme)}>{adminName}</Tag>
      </div>

      <div css={feedbackProgressStatus(theme)}>
        {!isStatisticsLoading && (
          <>
            <div css={progressStatus(theme)}>
              <p>전체 처리 현황</p>
              <p>
                {statistics.confirmedCount} / {statistics.totalCount}
              </p>
            </div>
            <StatusBar status={statistics.reflectionRate || 0} />
            <p css={progressStatusSummary(theme)}>
              {statistics.reflectionRate}% 완료 - {remainingCount}개 남음
            </p>
          </>
        )}
      </div>
    </div>
  );
}
