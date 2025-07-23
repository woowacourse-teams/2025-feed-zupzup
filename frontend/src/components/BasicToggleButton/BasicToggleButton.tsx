import { useAppTheme } from '@/hooks/useAppTheme';
import {
  circleStyle,
  toggleWrapper,
  trackStyle,
} from './BasicToggleButton.style';
import Button from '@/components/@commons/Button/Button';
import React from 'react';

interface ToggleButtonProps extends React.ComponentProps<'button'> {
  isToggled: boolean;
  onClick: () => void;
}

export default function BasicToggleButton({
  name,
  isToggled,
  onClick,
}: ToggleButtonProps) {
  const theme = useAppTheme();
  return (
    <Button css={toggleWrapper} onClick={onClick} name={name}>
      <div css={trackStyle(theme, isToggled)} />
      <div css={circleStyle(isToggled)} />
    </Button>
  );
}
