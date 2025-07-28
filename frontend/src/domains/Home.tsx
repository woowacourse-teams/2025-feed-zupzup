import OnBoarding from '@/domains/components/OnBoarding/OnBoarding';
import { css } from '@emotion/react';

export default function Home() {
  return <section css={container}>{<OnBoarding />}</section>;
}

const container = css`
  width: 100%;
  height: 100%;
`;
