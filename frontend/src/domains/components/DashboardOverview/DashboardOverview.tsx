import CheerButton from '@/domains/components/CheerButton/CheerButton';
import DashboardPanel from '@/domains/components/DashboardPanel/DashboardPanel';
import {
  headerContainer,
  cheerButtonLayout,
  panelCaption,
  panelLayout,
  titleText,
  headerText,
  headerCheerButton,
} from '@/domains/components/DashboardOverview/DashboardOverview.style';

import { useAppTheme } from '@/hooks/useAppTheme';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import useUserOrganizationsStatistics from '@/domains/hooks/useUserOrganizationsStatistics';

export default function DashboardOverview() {
  const theme = useAppTheme();
  const { groupName, totalCheeringCount } = useOrganizationName();
  const { statistics } = useUserOrganizationsStatistics();

  const DASH_PANELS = [
    {
      title: '반영률',
      content: `${statistics?.reflectionRate}%`,
      caption: `총 ${statistics?.confirmedCount}개 반영`,
    },
    {
      title: '총 건의 수',
      content: statistics?.totalCount,
      caption: '접수 완료',
    },
    { title: '미처리', content: statistics?.waitingCount, caption: '반영 전' },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '평균 2.5일',
    },
  ];

  return (
    <>
      <div css={headerContainer}>
        <div css={headerText}>
          <p css={titleText(theme)}>{groupName}</p>
          <p css={panelCaption(theme)}>일주일 간의 피드백</p>
        </div>
        <div css={headerCheerButton}>
          <div css={cheerButtonLayout}>
            <CheerButton totalCheeringCount={totalCheeringCount} />
          </div>
        </div>
      </div>
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
    </>
  );
}
