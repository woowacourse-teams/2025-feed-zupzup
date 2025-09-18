import OverviewHeader from './OverviewHeader/OverviewHeader';
import DashboardPanelContent from './DashboardPanelContent/DashboardPanelContent';
import { memo } from 'react';

export default memo(function DashboardOverview() {
  return (
    <>
      <OverviewHeader />
      <DashboardPanelContent />
    </>
  );
});
