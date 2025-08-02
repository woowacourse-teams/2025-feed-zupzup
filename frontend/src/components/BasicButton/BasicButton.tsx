import { useAppTheme } from '../../hooks/useAppTheme';

import {
  basicButton,
  basicButtonText,
  basicButtonIcon,
} from './BasicButton.styles';
import Button from '@/components/@commons/Button/Button';

export interface BasicButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  width?: string | number;
  height?: string | number;
  icon?: React.ReactNode;
  variant?: 'primary' | 'secondary' | 'disabled';
}

export default function BasicButton({
  children,
  width = '100%',
  height = '54px',
  icon,
  onClick,
  variant = 'primary',
}: BasicButtonProps) {
  const theme = useAppTheme();

  return (
    <Button css={basicButton(theme, width, height, variant)} onClick={onClick}>
      {icon && <span css={basicButtonIcon}>{icon}</span>}
      <span css={basicButtonText(theme, variant)}>{children}</span>
    </Button>
  );
}
