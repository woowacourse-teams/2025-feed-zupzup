import Button from '@/components/@commons/Button/Button';
import ArrowRightIcon from '@/components/icons/ArrowRightIcon';
import {
  adminOrganizationContainer,
  organizationTitle,
  organizationTitleContainer,
  pendingText,
} from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization.style';
import { dot } from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { formatRelativeTime } from '@/utils/formatRelativeTime';

export interface AdminOrganizationProps {
  organizationName: string;
  waitingCount: number;
  postedAt: string;
  onClick: () => void;
}
export default function AdminOrganization({
  organizationName,
  waitingCount,
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
      <p css={pendingText(theme)}>
        <strong>{waitingCount}</strong> 건 미처리
      </p>
      {postedAt ? (
        <p>최근 피드백: {formatRelativeTime(postedAt)}</p>
      ) : (
        <p>최근 피드백: 없음</p>
      )}
    </Button>
  );
}
