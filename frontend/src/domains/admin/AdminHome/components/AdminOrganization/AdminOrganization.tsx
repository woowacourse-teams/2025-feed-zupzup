import Button from '@/components/@commons/Button/Button';
import ArrowRightIcon from '@/components/icons/ArrowRightIcon';
import {
  adminOrganizationContainer,
  organizationTitle,
  organizationTitleContainer,
  organizationCountContainer,
  pendingText,
  smallText,
} from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization.style';
import { dot } from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { formatRelativeTime } from '@/utils/formatRelativeTime';

export interface AdminOrganizationProps {
  organizationName: string;
  waitingCount: number;
  postedAt: string;
  confirmedCount: number;
  onClick: () => void;
}
export default function AdminOrganization({
  organizationName,
  waitingCount,
  confirmedCount,
  postedAt,
  onClick,
}: AdminOrganizationProps) {
  const theme = useAppTheme();

  return (
    <Button css={adminOrganizationContainer(theme)} onClick={onClick}>
      <div css={organizationTitleContainer}>
        <div css={organizationTitle(theme)}>
          <div css={dot(theme)} />
          <span>{organizationName}</span>
        </div>
        <ArrowRightIcon />
      </div>
      <div css={organizationCountContainer}>
        <p css={pendingText(theme)}>
          <strong>{waitingCount}</strong> 건 미처리
        </p>
        <p css={smallText(theme)}>
          <strong>{confirmedCount}</strong> 건 완료
        </p>
      </div>
      {postedAt ? (
        <p>최근 피드백: {formatRelativeTime(postedAt)}</p>
      ) : (
        <p>최근 피드백: 없음</p>
      )}
    </Button>
  );
}
