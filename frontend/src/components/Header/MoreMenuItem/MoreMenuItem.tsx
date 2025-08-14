import {
  moreMenuItemContainer,
  moreMenuItemText,
} from '@/components/Header/MoreMenuItem/MoreMenuItem.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface MoreMenuItemProps {
  icon: React.ReactNode | string;
  menu: string;
}

export default function MoreMenuItem({ icon, menu }: MoreMenuItemProps) {
  const theme = useAppTheme();

  return (
    <div css={moreMenuItemContainer}>
      <div>{icon}</div>
      <div css={moreMenuItemText(theme)}>{menu}</div>
    </div>
  );
}
