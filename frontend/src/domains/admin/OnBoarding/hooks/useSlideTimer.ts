import { useEffect, useRef, useState } from 'react';

interface UseSlideTimerProps {
  totalSlides: number;
  intervalDuration?: number;
}

export const useSlideTimer = ({
  totalSlides,
  intervalDuration = 4000,
}: UseSlideTimerProps) => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  const startTimer = () => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }

    intervalRef.current = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % totalSlides);
    }, intervalDuration);
  };

  const stopTimer = () => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
  };

  const resetTimer = () => {
    stopTimer();
    startTimer();
  };

  const handleSlideChange = (index: number) => {
    setCurrentSlide(index);
    resetTimer();
  };

  useEffect(() => {
    startTimer();

    return () => {
      stopTimer();
    };
  }, [totalSlides, intervalDuration]);

  return {
    currentSlide,
    handleSlideChange,
  };
};
