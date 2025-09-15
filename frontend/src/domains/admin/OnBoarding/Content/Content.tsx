import { AnimatePresence, motion } from 'motion/react';
import QRFeedbackScene from '../scenes/QRFeedbackScene';
import IntuitiveInterfaceScene from '../scenes/IntuitiveInterfaceScene';
import RealTimeNotificationScene from '../scenes/RealTimeNotificationScene';
import AnalyticsManagementScene from '../scenes/AnalyticsManagementScene';
import { useEffect, useState } from 'react';
import { OnboardingSlide } from '../OnBoardingSlide/OnBoardingSlide';
import {
  container,
  slideContainer,
  paginationContainer,
  paginationDot,
  contentContainer,
} from './Content.styles';

const slides = [
  {
    id: 1,
    title: 'QR코드와 URL 관리',
    subtitle: (
      <>
        QR코드와 URL로 피드백을 <br /> 쉽게 수집해요
      </>
    ),
    icon: <QRFeedbackScene />,
  },
  {
    id: 2,
    title: '직관적인 인터페이스',
    subtitle: (
      <>
        직관적인 인터페이스로 피드백 <br /> 작성이 간편해요
      </>
    ),
    icon: <IntuitiveInterfaceScene />,
  },
  {
    id: 3,
    title: '실시간 알림',
    subtitle: (
      <>
        실시간 알림으로 새 피드백을 <br /> 즉시 확인해요
      </>
    ),
    icon: <RealTimeNotificationScene />,
  },
  {
    id: 4,
    title: '피드백 분석 관리',
    subtitle: (
      <>
        수집된 피드백을 분석하고 <br />
        관리할 수 있어요
      </>
    ),
    icon: <AnalyticsManagementScene />,
  },
];

export default function Content() {
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % slides.length);
    }, 4000);

    return () => clearInterval(interval);
  }, []);

  const handleDotClick = (index: number) => {
    setCurrentSlide(index);
  };

  return (
    <div css={contentContainer}>
      <div css={container}>
        <AnimatePresence>
          <motion.div
            key={currentSlide}
            initial={{ x: '100%' }}
            animate={{ x: 0 }}
            exit={{ x: '-100%' }}
            transition={{ duration: 0.6, ease: 'easeInOut' }}
            css={slideContainer}
          >
            <OnboardingSlide
              title={slides[currentSlide].title}
              subtitle={slides[currentSlide].subtitle}
              icon={slides[currentSlide].icon}
              isActive={true}
            />
          </motion.div>
        </AnimatePresence>
      </div>

      {/* Pagination Dots */}
      <div css={paginationContainer}>
        {slides.map((_, index) => (
          <motion.button
            key={index}
            onClick={() => handleDotClick(index)}
            css={paginationDot}
            style={{
              backgroundColor: index === currentSlide ? '#7356ff' : '#d1d5db',
            }}
            whileHover={{ scale: 1.2 }}
            whileTap={{ scale: 0.9 }}
          />
        ))}
      </div>
    </div>
  );
}
