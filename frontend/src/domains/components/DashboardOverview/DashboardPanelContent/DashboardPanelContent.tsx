import DashboardPanel from '@/domains/components/DashboardPanel/DashboardPanel';
import { StatisticsProps } from '@/domains/hooks/useUserOrganizationsStatistics';
import { useAppTheme } from '@/hooks/useAppTheme';
import { panelLayout } from './DashboardPanelContent.style';

interface DashboardPanelContentProps {
  statistics: StatisticsProps;
}

export default function DashboardPanelContent({
  statistics,
}: DashboardPanelContentProps) {
  const theme = useAppTheme();

  const DASH_PANELS = [
    {
      title: '반영률',
      content: `${statistics?.reflectionRate}%`,
      caption: `총 ${statistics?.confirmedCount}개 반영`,
      srMessage: `반영률 ${statistics?.reflectionRate}퍼센트`,
    },
    {
      title: '총 건의',
      content: statistics?.totalCount,
      caption: '접수 완료',
      srMessage: `총 건의 수 ${statistics?.totalCount}건`,
    },
    {
      title: '미처리',
      content: statistics?.waitingCount,
      caption: '반영 전',
      color: theme.colors.red[100],
      srMessage: `미처리 건수 ${statistics?.waitingCount}건`,
    },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '반영 완료',
      color: theme.colors.green[100],
      srMessage: `완료 건수 ${statistics?.confirmedCount}건`,
    },
  ];

  return (
    <div css={panelLayout}>
      {DASH_PANELS.map((panel, idx) => (
        <div key={idx}>
          <span className='srOnly'>{panel.srMessage}</span>
          <DashboardPanel
            title={panel.title}
            content={panel.content}
            caption={panel.caption}
            color={panel.color}
          />
        </div>
      ))}
    </div>
  );
}
