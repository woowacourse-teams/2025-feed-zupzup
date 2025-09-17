import Button from '@/components/@commons/Button/Button';
import FillHeartIcon from '@/components/icons/FillHeartIcon';
import {
  cheerButtonStyle,
  clickedStyle,
  iconWrapperStyle,
  textStyle,
} from '@/domains/components/CheerButton/CheerButton.style';
import { theme } from '@/theme';
import { useState } from 'react';

export interface CheerButtonProps {
  totalCheeringCount: number;
  onClick?: () => void;
  animate?: boolean;
  disabled?: boolean;
}

export default function CheerButton({
  totalCheeringCount,
  onClick,
  animate = false,
  disabled = false,
}: CheerButtonProps) {
  const [accCount, setAccCount] = useState(0);

  return (
    <Button
      onClick={() => {
        setAccCount((prev) => prev + 1);
        onClick?.();
      }}
      css={cheerButtonStyle}
      disabled={disabled}
    >
      <span css={[iconWrapperStyle(theme, disabled), animate && clickedStyle]}>
        <FillHeartIcon />
      </span>
      <p css={textStyle(theme)}>응원 {totalCheeringCount + accCount}</p>
    </Button>
  );
}
