import BasicButton from '@/components/BasicButton/BasicButton';
import PlusIcon from '@/components/icons/Plus';
import {
  hero,
  heroContent,
  heroHr,
  heroTitle,
  heroLogo,
  heroDescription,
} from './Hero.styles';
import feedzupzupLogo from '@/assets/images/feedzupzup-logo.png';

export default function Hero() {
  return (
    <div css={hero}>
      <div css={heroContent}>
        <hr css={heroHr} />
        <h1 css={heroTitle}>우테코 건의</h1>
        <img src={feedzupzupLogo} alt='피드줍줍 로고' css={heroLogo} />
        <p css={heroDescription}>
          더 나은 카페를 만들기 위해
          <br />
          여러분의 목소리가 필요합니다
        </p>
        <BasicButton
          onClick={() => console.log('clicked')}
          icon={<PlusIcon />}
          variant='primary'
        >
          건의하기
        </BasicButton>
      </div>
    </div>
  );
}
