import BasicButton from '@/components/BasicButton/BasicButton';
import ArrowLeftIcon from '@/components/icons/ArrowLeftIcon';
import SendIcon from '@/components/icons/SendIcon';
import { useState, useCallback } from 'react';
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
import TimeDelayModal from '@/components/TimeDelayModal/TimeDelayModal';
import { Analytics, suggestionFormEvents } from '@/analytics';
import { CategoryType } from '@/analytics/types';

interface FeedbackPageProps {
  category: CategoryType | null;
  movePrevStep: () => void;
}

export default function FeedbackPage({
  movePrevStep,
  category,
}: FeedbackPageProps) {
  const theme = useAppTheme();
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);

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

  const { submitFeedback, submitStatus } = useFeedbackSubmit();

  const handleSkipAndNavigate = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();

    Analytics.track(suggestionFormEvents.viewSuggestionsFromForm());

    navigate('/dashboard');
  };

  const handleRandomChangeWithTracking = () => {
    Analytics.track(suggestionFormEvents.randomNicknameClick());

    handleRandomChange();
  };

  const handleLockToggleWithTracking = () => {
    Analytics.track(suggestionFormEvents.privacyToggle(!isLocked));

    handleLockToggle();
  };

  const handleModalClose = useCallback(
    (isError: boolean) => {
      setIsModalOpen(false);
      if (!isError) {
        navigate('/dashboard');
      }
    },
    [navigate]
  );

  const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!canSubmit || submitStatus === 'submitting') {
      return;
    }

    try {
      Analytics.track(suggestionFormEvents.formSubmit());

      setIsModalOpen(true);

      await submitFeedback({
        content: feedback,
        userName: username,
        isSecret: isLocked,
        category,
      });
    } catch (error) {
      setIsModalOpen(false);
      console.error('피드백 제출 실패:', error);
    }
  };

  const isSubmitting = submitStatus === 'submitting';

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
            onRandomChange={handleRandomChangeWithTracking}
            onLockToggle={handleLockToggleWithTracking}
            onUsernameChange={handleUsernameChange}
            onUsernameFocus={handleUsernameFocus}
          />
        </div>

        <div css={buttonGroupContainer}>
          <BasicButton
            type='submit'
            disabled={!canSubmit || isSubmitting || isModalOpen}
            variant={
              canSubmit && !isSubmitting && !isModalOpen
                ? 'primary'
                : 'disabled'
            }
            icon={
              <SendIcon
                color={
                  canSubmit && !isSubmitting && !isModalOpen
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
            disabled={isSubmitting || isModalOpen}
          >
            <p css={skipText(theme)}>건의 목록 보러가기</p>
          </BasicButton>
        </div>
      </form>

      <TimeDelayModal
        isOpen={isModalOpen}
        onClose={() => handleModalClose(submitStatus === 'error')}
        loadingDuration={800}
        autoCloseDuration={1000}
        loadingMessage='피드백을 전송하고 있어요...'
        completeMessage='소중한 의견 감사해요!'
        width={320}
        height={200}
        modalStatus={submitStatus}
      />
    </section>
  );
}
