import Button from '@/components/@commons/Button/Button';
import {
  moreMenuItemContainer,
  moreMenuItemIcon,
  moreMenuItemText,
  progressBackground,
  progressOverlay,
} from '@/components/Header/styles/MoreMenuItem.styles';

import { useAppTheme } from '@/hooks/useAppTheme';

interface ProgressMenuItemProps extends React.ComponentProps<'button'> {
  icon: React.ReactNode | string;
  menu: string;
  onClick?: () => void;
  progress: number;
}

export default function ProgressMenuItem({
  icon,
  menu,
  onClick,
  disabled = false,
  progress,
}: ProgressMenuItemProps) {
  const theme = useAppTheme();

  return (
    <div css={progressOverlay}>
      <div css={progressBackground(theme, progress, disabled)} />
      <Button
        css={[moreMenuItemContainer(theme, disabled)]}
        onClick={onClick}
        role='button'
        disabled={disabled}
      >
        <div css={moreMenuItemIcon}>{icon}</div>
        <div css={moreMenuItemText(theme)}>
          {menu} {disabled && progress + '%'}
        </div>
      </Button>
    </div>
  );
}
