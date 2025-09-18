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
import useCheerButton from '@/domains/hooks/useCheerButton';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';

export default function DashboardOverview() {
  const { organizationId } = useOrganizationId();
  const theme = useAppTheme();

  const { groupName, totalCheeringCount } = useOrganizationName({
    organizationId,
  });
  const { statistics } = useUserOrganizationsStatistics({
    organizationId,
  });

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
    {
      title: '미처리',
      content: statistics?.waitingCount,
      caption: '반영 전',
      color: theme.colors.red[100],
    },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '반영 완료',
      color: theme.colors.green[100],
    },
  ];

  const { handleCheerButton, animate, isDisabled } = useCheerButton({
    organizationId,
  });

  return (
    <>
      <div css={headerContainer}>
        <div css={headerText}>
          <p css={titleText(theme)}>{groupName}</p>
          <p css={panelCaption(theme)}>지금까지의 피드백</p>
        </div>
        <div css={headerCheerButton}>
          <div css={cheerButtonLayout}>
            <CheerButton
              totalCheeringCount={totalCheeringCount}
              onClick={handleCheerButton}
              animate={animate}
              disabled={isDisabled}
            />
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
          />
        ))}
      </div>
    </>
  );
}
