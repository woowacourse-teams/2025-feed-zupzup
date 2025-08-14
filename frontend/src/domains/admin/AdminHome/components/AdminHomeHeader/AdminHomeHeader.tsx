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

const ADMIN = '관리자1';
const COMPLETED_COUNT = 17;
const TOTAL_COUNT = 29;
// 나중에 api 조회로 바꿀 값

export default function AdminHomeHeader() {
  const theme = useAppTheme();

  const completeStatus = Number(
    ((COMPLETED_COUNT / TOTAL_COUNT) * 100).toFixed(1)
  );
  const remainingCount = TOTAL_COUNT - COMPLETED_COUNT;

  return (
    <div css={headerLayout(theme)}>
      <div css={homeCaptionContainer(theme)}>
        <p css={homeTitle(theme)}>피드줍줍</p>
        <Tag customCSS={homeTag(theme)}>{ADMIN}</Tag>
      </div>

      <div css={feedbackProgressStatus(theme)}>
        <div css={progressStatus(theme)}>
          <p>전체 처리 현황</p>
          <p>
            {COMPLETED_COUNT} / {TOTAL_COUNT}
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
