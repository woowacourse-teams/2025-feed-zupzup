import { postOrganizationCheer } from '@/apis/organization.api';
import Button from '@/components/@commons/Button/Button';
import EmptyHeartIcon from '@/components/icons/EmptyHeartIcon';
import FillHeartIcon from '@/components/icons/FillHeartIcon';
import {
  cheerButtonStyle,
  clickedStyle,
  iconWrapperStyle,
  textStyle,
} from '@/domains/components/CheerButton/CheerButton.style';
import { useDebounce } from '@/hooks/useDebounce';
import { theme } from '@/theme';
import { useState } from 'react';

interface CheerButtonProps {
  totalCheeringCount: number;
}

export default function CheerButton({ totalCheeringCount }: CheerButtonProps) {
  const [count, setCount] = useState(0);
  const [accCount, setAccCount] = useState(0);
  const [clicked, setClicked] = useState(false);
  const [animate, setAnimate] = useState(false);

  const handleClick = () => {
    setCount(count + 1);
    setAccCount(accCount + 1);
    setClicked(true);
    setAnimate(true);
    debouncedSearch(count + 1);
  };

  const debouncedSearch = useDebounce(
    ((count: number) => {
      postOrganizationCheer({
        organizationId: 1,
        cheeringCount: count,
      });
      setCount(0);
    }) as (...args: unknown[]) => void,
    500
  );

  return (
    <Button onClick={handleClick} css={cheerButtonStyle}>
      <span css={[iconWrapperStyle, animate && clickedStyle]}>
        {clicked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </span>
      <p css={textStyle(theme)}>응원 {totalCheeringCount + accCount}</p>
    </Button>
  );
}
