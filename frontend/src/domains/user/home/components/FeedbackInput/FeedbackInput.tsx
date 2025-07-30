import { useAppTheme } from '@/hooks/useAppTheme';
import { useFeedbackForm } from '@/domains/user/home/hooks/useFeedbackForm';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';
import {
  container,
  formContainer,
  userInfo,
  randomButton,
  topInputBorder,
  textareaContainer,
  textarea,
  toggleButtonContainer,
  submitButtonContainer,
  toggleButtonText,
} from './FeedbackInput.styles';

import BasicButton from '@/components/BasicButton/BasicButton';
import SendIcon from '@/components/icons/SendIcon';
import Button from '@/components/@commons/Button/Button';
import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';

export interface FeedbackInputProps {
  className?: string;
}

export default function FeedbackInput({ className }: FeedbackInputProps) {
  const theme = useAppTheme();
  const {
    feedback,
    username,
    isLocked,
    handleFeedbackChange,
    handleRandomChange,
    handleLockToggle,
    canSubmit,
    handleSubmit,
  } = useFeedbackForm();

  return (
    <div css={container} className={className}>
      <div css={formContainer}>
        <div css={userInfo(theme)}>
          <p>{username}</p>
        </div>

        <Button
          onClick={handleRandomChange}
          disabled={isLocked}
          css={randomButton(theme)}
        >
          랜덤변경
        </Button>

        <div css={topInputBorder(theme)} />

        <div css={textareaContainer(theme)}>
          <TextArea
            value={feedback}
            onChange={handleFeedbackChange}
            placeholder={FEEDBACK_INPUT_CONSTANTS.PLACEHOLDER}
            customCSS={textarea(theme)}
            maxLength={FEEDBACK_INPUT_CONSTANTS.DEFAULTS.MAX_LENGTH}
            minLength={FEEDBACK_INPUT_CONSTANTS.DEFAULTS.MIN_LENGTH}
          />
        </div>
      </div>
      <div css={toggleButtonContainer}>
        <BasicToggleButton
          isToggled={isLocked}
          onClick={handleLockToggle}
          name='lock'
        />
        <p css={toggleButtonText(theme)}>비밀글로 작성</p>
      </div>
      <div css={submitButtonContainer}>
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
          onClick={handleSubmit}
        >
          피드백 제출
        </BasicButton>
      </div>
    </div>
  );
}
