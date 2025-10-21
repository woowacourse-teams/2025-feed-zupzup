import { useAppTheme } from '../../hooks/useAppTheme';

import {
  basicButton,
  basicButtonText,
  basicButtonIcon,
} from './BasicButton.styles';
import Button from '@/components/@commons/Button/Button';

export interface BasicButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  width?: string;
  height?: string;
  padding?: string;
  fontSize?: string;
  icon?: React.ReactNode;
  gap?: string;
  variant?: 'primary' | 'secondary' | 'disabled';
}

export default function BasicButton({
  children,
  width = '100%',
  height = '54px',
  padding,
  fontSize,
  icon,
  onClick,
  gap = '14px',
  variant = 'primary',
  disabled,
  type = 'button',
  ...rest
}: BasicButtonProps) {
  const theme = useAppTheme();

  return (
    <Button
      css={basicButton(theme, width, variant, height, gap, padding)}
      onClick={onClick}
      disabled={disabled}
      type={type}
      {...rest}
    >
      {icon && <span css={basicButtonIcon}>{icon}</span>}
      <span css={basicButtonText(theme, variant, fontSize)}>{children}</span>
    </Button>
  );
}
