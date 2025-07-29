import Button from '@/components/@commons/Button/Button';
import EmptyHeartIcon from '@/components/icons/EmptyHeartIcon';
import FillHeartIcon from '@/components/icons/FillHeartIcon';
import {
  cheerButtonStyle,
  clickedStyle,
  iconWrapperStyle,
  textStyle,
} from '@/domains/components/CheerButton/CheerButton.style';
import { theme } from '@/theme';
import { useState } from 'react';

export default function CheerButton() {
  const [count, setCount] = useState(0);
  const [clicked, setClicked] = useState(false);
  const [animate, setAnimate] = useState(false);

  const handleClick = () => {
    setCount(count + 1);
    setClicked(true);
    setAnimate(true);
    setTimeout(() => setAnimate(false), 300);
  };

  return (
    <Button onClick={handleClick} css={cheerButtonStyle}>
      <span css={[iconWrapperStyle, animate && clickedStyle]}>
        {clicked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </span>
      <p css={textStyle(theme)}>응원 {count}</p>
    </Button>
  );
}
