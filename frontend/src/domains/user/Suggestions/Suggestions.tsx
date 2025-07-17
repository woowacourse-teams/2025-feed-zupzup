import Header from '@/components/Header/Header';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import Banner from './components/Banner/Banner';

export default function Suggestions() {
  const theme = useAppTheme();

  return (
    <>
      <Header title='건의사항 작성' subtitle='소중한 의견을 들려주세요' />
      <div css={suggestions(theme)}>
        <Banner
          title='더 나은 서비스를 위해'
          description='여러분의 소중한 의견이 더 좋은 카페를 만들어
갑니다'
        />
      </div>
    </>
  );
}

const suggestions = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 100vh;
  margin-top: 64px; /* Header height */
  background-color: ${theme.colors.white[300]};
`;
