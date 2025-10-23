import Button from '@/components/@commons/Button/Button';
import {
  moreMenuItemContainer,
  moreMenuItemIcon,
  moreMenuItemText,
} from '@/components/Header/MoreMenuItem/MoreMenuItem.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface MoreMenuItemProps {
  icon: React.ReactNode | string;
  menu: string;
  onClick?: () => void;
}

export default function MoreMenuItem({
  icon,
  menu,
  onClick,
}: MoreMenuItemProps) {
  const theme = useAppTheme();

  return (
    <Button css={moreMenuItemContainer(theme)} onClick={onClick} role='button'>
      <div css={moreMenuItemIcon}>{icon}</div>
      <div css={moreMenuItemText(theme)}>{menu}</div>
    </Button>
  );
}
