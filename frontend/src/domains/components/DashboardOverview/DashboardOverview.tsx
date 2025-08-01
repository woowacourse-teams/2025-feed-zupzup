import CheerButton from '@/domains/components/CheerButton/CheerButton';
import DashboardPanel from '@/domains/components/DashboardPanel/DashboardPanel';
import {
  cheerButtonLayout,
  panelCaption,
  panelLayout,
  titleText,
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
      content: statistics?.reflectionRate,
      caption: '총 2개 반영',
    },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '평균 2.5일',
    },
    { title: '미처리', content: statistics?.waitingCount, caption: '반영 전' },
    {
      title: '총 건의 수',
      content: statistics?.totalCount,
      caption: '반영 완료',
    },
  ];

  return (
    <>
      <p css={titleText(theme)}>{groupName}</p>
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
      <div css={cheerButtonLayout}>
        <CheerButton totalCheeringCount={totalCheeringCount} />
      </div>
    </>
  );
}
