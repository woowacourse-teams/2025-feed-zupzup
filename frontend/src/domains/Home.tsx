import { SEO } from '@/components/SEO/SEO';
import ProgressBar from '@/components/ProgressBar/ProgressBar';
import useCategoryManager from '@/domains/hooks/useCategoryManager';
import FeedbackPage from '@/domains/user/FeedbackPage/FeedbackPage';
import OnBoarding from '@/domains/user/OnBoarding/OnBoarding';
import useProgressStep from '@/hooks/useProgressStep';
import { css } from '@emotion/react';

export default function Home() {
  const { currentStep, moveNextStep, movePrevStep, totalStep } =
    useProgressStep({
      totalStep: 2,
    });
  const { category, handleCategoryChange } = useCategoryManager({
    moveNextStep,
  });

  return (
    <>
      <SEO
        title='피드백 제출'
        description='자유롭게 의견을 남겨주세요'
        keywords='피드백, 의견, 제안, 건의, 제출, 신고'
      />
      <section css={container}>
        <ProgressBar currentStep={currentStep} totalStep={totalStep} />
        {currentStep === 1 && (
          <OnBoarding onCategoryClick={handleCategoryChange} />
        )}
        {currentStep === 2 && (
          <FeedbackPage movePrevStep={movePrevStep} category={category} />
        )}
      </section>
    </>
  );
}

const container = css`
  width: 100%;
  height: 100%;
`;
