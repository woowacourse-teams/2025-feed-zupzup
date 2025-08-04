import ProgressBar from '@/components/ProgressBar/ProgressBar';
import FeedbackPage from '@/domains/user/FeedbackPage/FeedbackPage';
import OnBoarding from '@/domains/user/OnBoarding/OnBoarding';
import useProgressStep from '@/hooks/useProgressStep';

import { css } from '@emotion/react';

export default function Home() {
  const { currentStep, moveNextStep, movePrevStep, totalStep } =
    useProgressStep({
      totalStep: 2,
    });

  return (
    <section css={container}>
      <button
        onClick={() => {
          throw new Error('This is your first error!');
        }}
      >
        Break the world
      </button>
      <ProgressBar currentStep={currentStep} totalStep={totalStep} />
      {currentStep === 1 && <OnBoarding moveNextStep={moveNextStep} />}
      {currentStep === 2 && <FeedbackPage movePrevStep={movePrevStep} />}
    </section>
  );
}

const container = css`
  width: 100%;
  height: 100%;
`;
