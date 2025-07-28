import ProgressBar from '@/components/ProgressBar/ProgressBar';
import OnBoarding from '@/domains/components/OnBoarding/OnBoarding';
import useProgressStep from '@/hooks/useProgressStep';
import { css } from '@emotion/react';

export default function Home() {
  const { currentStep, moveNextStep, totalStep } = useProgressStep({
    totalStep: 2,
  });
  return (
    <section css={container}>
      <ProgressBar currentStep={currentStep} totalStep={totalStep} />
      {currentStep === 1 && <OnBoarding moveNextStep={moveNextStep} />}
    </section>
  );
}

const container = css`
  width: 100%;
  height: 100%;
`;
