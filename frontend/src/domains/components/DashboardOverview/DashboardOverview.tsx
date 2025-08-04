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
import { getDashPanels } from '@/domains/components/DashboardOverview/DashboardOverview.utils';

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

  const DASH_PANELS = getDashPanels(statistics, theme, handlePanelClick);

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
