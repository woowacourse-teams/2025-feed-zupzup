import BasicButton from '@/components/BasicButton/BasicButton';
import PlusIcon from '@/components/icons/Plus';
import { heroStyle } from './Hero.styles';

export default function Hero() {
  return (
    <div css={heroStyle}>
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
