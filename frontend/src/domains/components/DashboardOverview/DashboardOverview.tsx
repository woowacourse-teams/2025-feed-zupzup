import OverviewHeader from './OverviewHeader/OverviewHeader';
import DashboardPanelContent from './DashboardPanelContent/DashboardPanelContent';
import React from 'react';

function DashboardOverview() {
  return (
    <>
      <OverviewHeader />
      <DashboardPanelContent />
    </>
  );
}

export default React.memo(DashboardOverview);
