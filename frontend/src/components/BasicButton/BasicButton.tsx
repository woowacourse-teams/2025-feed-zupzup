import { forwardRef } from 'react';
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

function BasicButtonComponent(
  {
    children,
    width = '100%',
    height = '54px',
    padding,
    fontSize,
    icon,
    gap = '14px',
    variant = 'primary',
    ...buttonProps
  }: BasicButtonProps,
  ref: React.ForwardedRef<HTMLButtonElement>
) {
  const theme = useAppTheme();

  return (
    <Button
      ref={ref}
      css={basicButton(theme, width, variant, height, gap, padding)}
      {...buttonProps}
    >
      {icon && <span css={basicButtonIcon}>{icon}</span>}
      <span css={basicButtonText(theme, variant, fontSize)}>{children}</span>
    </Button>
  );
}

const BasicButton = forwardRef(BasicButtonComponent);

export default BasicButton;
