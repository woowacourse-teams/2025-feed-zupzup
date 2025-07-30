import {
  container,
  subTitle,
  title,
  skipText,
  skipButtonContainer,
  mainTitle,
  titleContainer,
  contentContainer,
} from '@/domains/user/FeedbackPage/FeedbackPage.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import FeedbackInput from '@/domains/user/home/components/FeedbackInput/FeedbackInput';
import BasicButton from '@/components/BasicButton/BasicButton';
import SkipIcon from '@/components/icons/SkipIcon';

export default function FeedbackPage() {
  const theme = useAppTheme();

  return (
    <section css={container}>
      <div css={contentContainer}>
        <div css={titleContainer}>
          <span css={mainTitle(theme)}>소중한 의견</span>
          <span css={title(theme)}>을 들려주세요</span>
        </div>
        <p css={subTitle(theme)}>
          더 나은 공간을 만들기 위해 여러분의 <br />
          피드백이 필요해요
        </p>
      </div>
      <FeedbackInput />
      <div css={skipButtonContainer}>
        <BasicButton icon={<SkipIcon />} variant='secondary'>
          <p css={skipText(theme)}>건너뛰기</p>
        </BasicButton>
      </div>
    </section>
  );
}
