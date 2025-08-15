import Button from '@/components/@commons/Button/Button';
import {
  moreMenuItemContainer,
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
    <Button css={moreMenuItemContainer} onClick={onClick} role='button'>
      <div>{icon}</div>
      <div css={moreMenuItemText(theme)}>{menu}</div>
    </Button>
  );
}
