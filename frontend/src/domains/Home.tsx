import ProgressBar from '@/components/ProgressBar/ProgressBar';
import useCategoryManager from '@/domains/hooks/useCategoryManager';
import FeedbackPage from '@/domains/user/FeedbackPage/FeedbackPage';
import OnBoarding from '@/domains/user/OnBoarding/OnBoarding';
import useProgressStep from '@/hooks/useProgressStep';
import { css } from '@emotion/react';

export default function Home() {
  const { category, handleCategoryChange } = useCategoryManager({
    moveNextStep: () => moveNextStep(),
  });

  const { currentStep, moveNextStep, movePrevStep, totalStep } =
    useProgressStep({
      totalStep: 2,
    });

  return (
    <section css={container}>
      <ProgressBar currentStep={currentStep} totalStep={totalStep} />
      {currentStep === 1 && (
        <OnBoarding onCategoryClick={handleCategoryChange} />
      )}
      {currentStep === 2 && (
        <FeedbackPage movePrevStep={movePrevStep} category={category} />
      )}
    </section>
  );
}

const container = css`
  width: 100%;
  height: 100%;
`;
