import {
  container,
  title,
  skipText,
  mainTitle,
  titleContainer,
  contentContainer,
  arrowLeftIconContainer,
  buttonGroupContainer,
  mainContent,
} from '@/domains/user/FeedbackPage/FeedbackPage.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import FeedbackInput from '@/domains/user/home/components/FeedbackInput/FeedbackInput';
import BasicButton from '@/components/BasicButton/BasicButton';
import SkipIcon from '@/components/icons/SkipIcon';
import ArrowLeftIcon from '@/components/icons/ArrowLeftIcon';
import { useFeedbackForm } from '@/domains/user/home/hooks/useFeedbackForm';
import SendIcon from '@/components/icons/SendIcon';
import { useNavigate } from 'react-router-dom';

interface FeedbackPageProps {
  movePrevStep: () => void;
}

export default function FeedbackPage({ movePrevStep }: FeedbackPageProps) {
  const theme = useAppTheme();
  const navigate = useNavigate();
  const {
    feedback,
    username,
    isLocked,
    handleFeedbackChange,
    handleRandomChange,
    handleLockToggle,
    canSubmit,
    handleSubmit,
    handleUsernameChange,
    handleUsernameFocus,
  } = useFeedbackForm();

  const handleSubmitAndNavigate = () => {
    handleSubmit();
    navigate('/dashboard');
  };

  const handleSkipAndNavigate = () => {
    navigate('/dashboard');
  };

  return (
    <section css={container}>
      <div css={arrowLeftIconContainer} onClick={movePrevStep}>
        <ArrowLeftIcon width={30} height={30} strokeWidth={0.5} />
      </div>
      <div css={mainContent}>
        <div css={contentContainer}>
          <div css={titleContainer}>
            <span css={mainTitle(theme)}>소중한 의견</span>
            <span css={title(theme)}>을 들려주세요</span>
          </div>
        </div>

        <FeedbackInput
          feedback={feedback}
          username={username}
          isLocked={isLocked}
          canSubmit={canSubmit}
          onFeedbackChange={handleFeedbackChange}
          onRandomChange={handleRandomChange}
          onLockToggle={handleLockToggle}
          onSubmit={handleSubmit}
          onUsernameChange={handleUsernameChange}
          onUsernameFocus={handleUsernameFocus}
        />
      </div>

      <div css={buttonGroupContainer}>
        <BasicButton
          type='submit'
          disabled={!canSubmit}
          variant={canSubmit ? 'primary' : 'disabled'}
          icon={
            <SendIcon
              color={
                canSubmit ? theme.colors.white[100] : theme.colors.gray[500]
              }
            />
          }
          onClick={handleSubmitAndNavigate}
        >
          피드백 제출
        </BasicButton>

        <BasicButton
          icon={<SkipIcon />}
          variant='secondary'
          onClick={handleSkipAndNavigate}
        >
          <p css={skipText(theme)}>건너뛰기</p>
        </BasicButton>
      </div>
    </section>
  );
}
