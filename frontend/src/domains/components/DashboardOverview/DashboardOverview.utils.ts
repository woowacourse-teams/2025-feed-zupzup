import { StatisticsProps } from '@/domains/hooks/useUserOrganizationsStatistics';
import { Theme } from '@/theme';
import { FeedbackFilterType } from '@/types/feedback.types';

export function getDashPanels(
  statistics: StatisticsProps,
  theme: Theme,
  handlePanelClick: (category: FeedbackFilterType) => void
) {
  return [
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
      onClick: () => handlePanelClick('PENDING'),
      isButton: true,
    },
    {
      title: '완료',
      content: statistics?.confirmedCount,
      caption: '반영 완료',
      color: theme.colors.green[100],
      onClick: () => handlePanelClick('COMPLETED'),
      isButton: true,
    },
  ];
}
