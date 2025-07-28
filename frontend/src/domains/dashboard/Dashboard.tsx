import DashboardPanel from '@/domains/dashboard/components/DashboardPanel/DashboardPanel';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

const GROUP_NAME = '우아한테크코스';

const DASH_PANELS = [
  { title: '반영률', content: '40%', caption: '총 2개 반영' },
  { title: '완료', content: '2', caption: '평균 2.5일' },
  { title: '미처리', content: '1', caption: '반영 전' },
  { title: '총 건의 수', content: '3', caption: '반영 완료' },
];

export default function Dashboard() {
  const theme = useAppTheme();
  return (
    <div css={dashboardLayout}>
      <p css={titleText(theme)}>{GROUP_NAME}</p>
      <div>
        <p css={panelCaption(theme)}>일주일 간의 피드백</p>
        <div css={panelLayout}>
          {DASH_PANELS.map((panel, idx) => (
            <DashboardPanel
              key={idx}
              title={panel.title}
              content={panel.content}
              caption={panel.caption}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

const panelCaption = (theme: Theme) => css`
  margin-bottom: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.inter.caption}
`;

const panelLayout = css`
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, 1fr);
`;

const titleText = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyRegular};

  font-weight: 900;
`;
