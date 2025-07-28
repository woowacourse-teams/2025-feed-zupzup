import Button from '@/components/@commons/Button/Button';
import EmptyHeartIcon from '@/components/icons/EmptyHeartIcon';
import FillHeartIcon from '@/components/icons/FillHeartIcon';
import { css } from '@emotion/react';
import { useState } from 'react';

export default function CheerButton() {
  const [count, setCount] = useState(0);
  const [clicked, setClicked] = useState(false);
  const [animate, setAnimate] = useState(false);

  const handleClick = () => {
    setCount(count + 1);
    setClicked(true);
    setAnimate(true);
    setTimeout(() => setAnimate(false), 300); // 애니메이션 클래스 제거
  };

  return (
    <Button onClick={handleClick} css={buttonStyle}>
      <span css={[iconWrapperStyle, animate && clickedStyle]}>
        {clicked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </span>
      <p css={textStyle}>응원 {count}</p>
    </Button>
  );
}

const buttonStyle = css`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 9999px;
`;

const textStyle = css`
  font-size: 14px;
  color: #4a4a4a;
`;

const iconWrapperStyle = css`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const clickedStyle = css`
  animation: pop 0.3s ease;

  @keyframes pop {
    0% {
      transform: scale(1);
    }

    50% {
      transform: scale(1.3);
    }

    100% {
      transform: scale(1);
    }
  }
`;
