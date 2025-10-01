import { SEO } from '@/components/SEO/SEO';
import ActionButtons from './ActionButtons/ActionButtons';
import Content from './Content/Content';
import { container, cardContainer } from './OnBoarding.styles';

export default function OnBoarding() {
  return (
    <>
      <SEO
        title='시작하기'
        description='피드백을 쉽고 남기고, 확실히 받는 방법을 알아보세요'
        keywords='소개, 가이드, 사용법, 피드줍줍'
      />

      <div css={container}>
        <div css={cardContainer}>
          <Content />
          <ActionButtons />
        </div>
      </div>
    </>
  );
}
