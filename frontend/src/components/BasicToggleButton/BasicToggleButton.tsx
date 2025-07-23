import { useAppTheme } from '@/hooks/useAppTheme';
import {
  circleStyle,
  toggleWrapper,
  trackStyle,
} from './BasicToggleButton.style';

interface ToggleButtonProps {
  isToggled: boolean;
  onClick: () => void;
}

export default function BasicToggleButton({
  isToggled,
  onClick,
}: ToggleButtonProps) {
  const theme = useAppTheme();
  return (
    <>
      <div css={toggleWrapper} onClick={onClick}>
        <div css={trackStyle(theme, isToggled)} />
        <div css={circleStyle(isToggled)} />
      </div>
    </>
  );
}
