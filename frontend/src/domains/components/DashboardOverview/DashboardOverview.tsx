import CheerButton from '@/domains/components/CheerButton/CheerButton';
import DashboardPanel from '@/domains/components/DashboardPanel/DashboardPanel';
import {
  cheerButtonLayout,
  panelCaption,
  panelLayout,
} from '@/domains/components/DashboardOverview/DashboardOverview.style';

import { useAppTheme } from '@/hooks/useAppTheme';
import { DASH_PANELS } from '@/domains/mocks/dashPanels.mock';

export default function DashboardOverview() {
  const theme = useAppTheme();

  return (
    <>
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
        <CheerButton />
      </div>
    </>
  );
}
