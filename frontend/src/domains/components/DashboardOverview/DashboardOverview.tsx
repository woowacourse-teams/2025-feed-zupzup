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
import { FeedbackFilter } from '@/types/feedback.types';

interface DashboardOverviewProps {
  filter: FeedbackFilter;
  handlePanelClick: (category: FeedbackFilter) => void;
}

export default function DashboardOverview({
  filter,
  handlePanelClick,
}: DashboardOverviewProps) {
  const theme = useAppTheme();
  const { groupName, totalCheeringCount } = useOrganizationName();
  const { statistics } = useUserOrganizationsStatistics();

  const DASH_PANELS = [
    {
      title: '반영률',
      content: `${statistics?.reflectionRate}%`,
      caption: `총 ${statistics?.confirmedCount}개 반영`,
      isButton: false,
    },
    {
      title: '총 건의 수',
      content: statistics?.totalCount,
      caption: '접수 완료',
      isButton: false,
    },
    {
      title: '미처리',
      content: statistics?.waitingCount,
      caption: '반영 전',
      color: theme.colors.red[100],
      onClick: () => handlePanelClick('미처리'),
      isButton: true,
    },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '반영 완료',
      color: theme.colors.green[100],
      onClick: () => handlePanelClick('완료'),
      isButton: true,
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
            color={panel.color}
            isClick={filter === panel.title}
            onClick={panel.onClick}
            isButton={panel.isButton}
          />
        ))}
      </div>
    </>
  );
}
