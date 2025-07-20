import { useAppTheme } from '@/hooks/useAppTheme';
import {
  circleStyle,
  toggleWrapper,
  trackStyle,
} from './BasicToggleButton.style';

interface ToggleButtonProps {
  isOn: boolean;
  onClick: () => void;
}

export default function BasicToggleButton({
  isOn,
  onClick,
}: ToggleButtonProps) {
  const theme = useAppTheme();
  return (
    <>
      <div css={toggleWrapper} onClick={onClick}>
        <div css={trackStyle(theme, isOn)} />
        <div css={circleStyle(isOn)} />
      </div>
    </>
  );
}
