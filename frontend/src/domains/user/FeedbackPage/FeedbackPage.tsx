import BasicButton from '@/components/BasicButton/BasicButton';
import ArrowLeftIcon from '@/components/icons/ArrowLeftIcon';
import SendIcon from '@/components/icons/SendIcon';
import {
  arrowLeftIconContainer,
  buttonGroupContainer,
  combinedTitle,
  container,
  contentContainer,
  mainContent,
  skipText,
  titleContainer,
} from '@/domains/user/FeedbackPage/FeedbackPage.styles';
import FeedbackInput from '@/domains/user/home/components/FeedbackInput/FeedbackForm';
import { useFeedbackForm } from '@/domains/user/home/hooks/useFeedbackForm';
import { skipIcon } from '@/domains/user/OnBoarding/OnBoarding.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useNavigate } from 'react-router-dom';
import useFeedbackSubmit from './hooks/useFeedbackSubmit';

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
    handleUsernameChange,
    handleUsernameFocus,
  } = useFeedbackForm();

  const { handleFormSubmit, isSubmitting } = useFeedbackSubmit();

  const handleSkipAndNavigate = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    navigate('/dashboard');
  };

  const onSubmit = handleFormSubmit(
    {
      content: feedback,
      userName: username,
      isSecret: isLocked,
    },
    canSubmit
  );

  return (
    <section css={container}>
      <div css={arrowLeftIconContainer} onClick={movePrevStep}>
        <ArrowLeftIcon />
      </div>

      <form css={mainContent} onSubmit={onSubmit}>
        <div>
          <div css={contentContainer}>
            <div css={titleContainer}>
              <span css={combinedTitle(theme)}>
                <strong>소중한 의견</strong>을 들려주세요
              </span>
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
            onUsernameChange={handleUsernameChange}
            onUsernameFocus={handleUsernameFocus}
          />
        </div>

        <div css={buttonGroupContainer}>
          <BasicButton
            type='submit'
            disabled={!canSubmit || isSubmitting}
            variant={canSubmit && !isSubmitting ? 'primary' : 'disabled'}
            icon={
              <SendIcon
                color={
                  canSubmit && !isSubmitting
                    ? theme.colors.white[100]
                    : theme.colors.gray[500]
                }
              />
            }
          >
            피드백 제출
          </BasicButton>

          <BasicButton
            type='button'
            icon={<p css={skipIcon}>📄</p>}
            variant='secondary'
            onClick={handleSkipAndNavigate}
            disabled={isSubmitting}
          >
            <p css={skipText(theme)}>건의 목록 보러가기</p>
          </BasicButton>
        </div>
      </form>
    </section>
  );
}
