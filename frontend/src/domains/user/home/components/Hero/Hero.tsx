import BasicButton from '../BasicButton/BasicButton';
import { heroStyle } from './Hero.styles';

export default function Hero() {
  return (
    <div css={heroStyle}>
      <BasicButton width={100} onClick={() => console.log('clicked')}>
        Click me
      </BasicButton>
    </div>
  );
}
