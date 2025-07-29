import { useEffect, useState } from 'react';

interface UserProgressStep {
  totalStep: number;
}

export default function useProgressStep({ totalStep }: UserProgressStep) {
  const [currentStep, setCurrentStep] = useState(0);

  useEffect(() => {
    setCurrentStep(1);
  }, []);

  const moveNextStep = () => {
    setCurrentStep((prev) => (prev < totalStep ? prev + 1 : prev));
  };

  const movePrevStep = () => {
    setCurrentStep((prev) => (prev > 1 ? prev - 1 : prev));
  };

  const isFinalStep = currentStep === totalStep;

  return {
    totalStep,
    currentStep,
    moveNextStep,
    movePrevStep,
    isFinalStep,
  };
}
