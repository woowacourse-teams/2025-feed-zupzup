import BasicButton from '@/components/BasicButton/BasicButton';
import PlusIcon from '@/components/icons/Plus';
import { hero } from './Hero.styles';

export default function Hero() {
  return (
    <div css={hero}>
      <BasicButton
        width={294}
        onClick={() => console.log('clicked')}
        icon={<PlusIcon />}
        variant='secondary'
      >
        건의하기
      </BasicButton>
    </div>
  );
}
