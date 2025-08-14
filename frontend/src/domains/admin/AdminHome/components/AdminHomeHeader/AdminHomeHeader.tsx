import Tag from '@/components/Tag/Tag';
import StatusBar from '@/domains/admin/AdminHome/components/StatusBar/StatusBar';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

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

export const headerLayout = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
  background-color: ${theme.colors.blue[100]};
`;

export const homeCaptionContainer = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  margin-top: 16px;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption};

  strong {
    color: ${theme.colors.purple[100]};
    cursor: pointer;
  }
`;

export const homeTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};

  color: ${theme.colors.white[100]};
`;

export const homeTag = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall};

  padding: 6px 12px;
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.white[100]}33;
`;

export const feedbackProgressStatus = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 20px;
  color: ${theme.colors.gray[500]};
  background-color: ${theme.colors.white[100]}22;
  border-radius: 12px;

  ${theme.typography.pretendard.captionSmall};
`;

export const progressBar = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 8px;
  background-color: ${theme.colors.gray[200]};
  border-radius: 4px;
  overflow: hidden;
`;

export const progressStatus = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[200]};

  > p:first-of-type {
    ${theme.typography.pretendard.captionBold};
  }
`;

export const progressStatusSummary = (theme: Theme) => css`
  display: flex;
  justify-content: start;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[300]};
  border-radius: 4px;

  ${theme.typography.pretendard.captionSmall};
`;
