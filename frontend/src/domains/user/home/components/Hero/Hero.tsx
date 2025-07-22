import BasicButton from '@/components/BasicButton/BasicButton';
import PlusIcon from '@/components/icons/PlusIcon';
import {
  hero,
  heroContent,
  heroHr,
  heroTitle,
  heroLogo,
  heroDescription,
  heroHeader,
  catAnimation,
} from './Hero.styles';
import feedzupzupLogo from '@/assets/images/feedzupzup-logo.png';
import catImage from '@/assets/images/cat.png';

import GhostButton from '@/components/GhostButton/GhostButton';
import ProfileIcon from '@/components/icons/ProfileIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
export interface HeroProps extends React.ComponentProps<'div'> {
  onLoginClick: () => void;
  onSuggestClick: () => void;
  title: string;
  showSuggestButton?: boolean;
}

export default function Hero({
  title,
  onLoginClick,
  onSuggestClick,
  showSuggestButton = true,
}: HeroProps) {
  const theme = useAppTheme();

  return (
    <div css={hero(theme)}>
      <div css={heroHeader(theme)}>
        <GhostButton
          icon={<ProfileIcon />}
          text='로그인'
          onClick={onLoginClick}
        />
      </div>
      <div css={heroContent}>
        <hr css={heroHr(theme)} />
        <p css={heroTitle(theme)}>{title} 건의</p>
        <img src={feedzupzupLogo} alt='피드줍줍 로고' css={heroLogo} />
        <p css={heroDescription(theme)}>
          더 나은 카페를 만들기 위해
          <br />
          여러분의 목소리가 필요합니다
        </p>
        <div style={{ position: 'relative', width: '100%' }}>
          <img
            src={catImage}
            alt='움직이는 고양이'
            css={catAnimation(showSuggestButton)}
          />
          {showSuggestButton && (
            <BasicButton
              width='100%'
              icon={<PlusIcon />}
              variant='primary'
              onClick={onSuggestClick}
            >
              건의하기
            </BasicButton>
          )}
        </div>
      </div>
    </div>
  );
}
