import Button from '@/components/@commons/Button/Button';
import {
  moreMenuItemContainer,
  moreMenuItemIcon,
  moreMenuItemText,
} from '@/components/Header/MoreMenuItem/MoreMenuItem.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface MoreMenuItemProps extends React.ComponentProps<'button'> {
  icon: React.ReactNode | string;
  menu: string;
  onClick?: () => void;
}

export default function MoreMenuItem({
  icon,
  menu,
  onClick,
  disabled = true,
}: MoreMenuItemProps) {
  const theme = useAppTheme();

  return (
    <Button
      css={moreMenuItemContainer(theme, disabled)}
      onClick={onClick}
      role='button'
      disabled={disabled}
    >
      <div css={moreMenuItemIcon}>{icon}</div>
      <div css={moreMenuItemText(theme)}>{menu}</div>
    </Button>
  );
}
