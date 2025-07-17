import BasicButton from '../../../../../components/BasicButton/BasicButton';
import { heroStyle } from './Hero.styles';
import PlusIcon from '../../../../../components/icons/Plus';

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
