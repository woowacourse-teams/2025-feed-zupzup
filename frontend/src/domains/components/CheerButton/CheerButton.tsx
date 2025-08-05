import Button from '@/components/@commons/Button/Button';
import FillHeartIcon from '@/components/icons/FillHeartIcon';
import {
  cheerButtonStyle,
  clickedStyle,
  iconWrapperStyle,
  textStyle,
} from '@/domains/components/CheerButton/CheerButton.style';
import useCheerButton from '@/domains/hooks/useCheerButton';
import { theme } from '@/theme';

interface CheerButtonProps {
  totalCheeringCount: number;
}

export default function CheerButton({ totalCheeringCount }: CheerButtonProps) {
  const { handleClick, animate, accCount } = useCheerButton();

  return (
    <Button onClick={handleClick} css={cheerButtonStyle}>
      <span css={[iconWrapperStyle, animate && clickedStyle]}>
        <FillHeartIcon />
      </span>
      <p css={textStyle(theme)}>응원 {totalCheeringCount + accCount}</p>
    </Button>
  );
}
