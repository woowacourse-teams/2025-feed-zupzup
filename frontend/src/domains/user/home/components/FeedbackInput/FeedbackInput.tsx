import LockIcon from '@/components/icons/LockIcon';
import UnlockIcon from '@/components/icons/UnlockIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useFeedbackForm } from '@/domains/user/home/hooks/useFeedbackForm';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';
import * as styles from './FeedbackInput.styles';
import BasicButton from '@/components/BasicButton/BasicButton';
import SendIcon from '@/components/icons/SendIcon';
import Button from '@/components/@commons/Button/Button';
import TextArea from '@/components/@commons/TextArea/TextArea';

export interface FeedbackInputProps {
  className?: string;
}

export default function FeedbackInput({ className }: FeedbackInputProps) {
  const theme = useAppTheme();
  const {
    feedback,
    username,
    isLocked,
    currentAvatar,
    handleFeedbackChange,
    handleRandomChange,
    handleLockToggle,
    canSubmit,
    handleSubmit,
  } = useFeedbackForm();

  return (
    <div css={styles.container} className={className}>
      <div css={styles.formContainer}>
        <div css={styles.userInfo(theme)}>
          <p>{username}</p>
        </div>

        <Button
          onClick={handleRandomChange}
          disabled={isLocked}
          css={styles.randomButton(theme)}
        >
          랜덤변경
        </Button>

        <div
          css={styles.avatar}
          style={{ backgroundImage: `url('${currentAvatar}')` }}
        />

        <div css={styles.topInputBorder(theme)} />

        <Button onClick={handleLockToggle} css={styles.lockButton(theme)}>
          {isLocked ? <LockIcon /> : <UnlockIcon />}
        </Button>

        <div css={styles.textareaContainer(theme)}>
          <TextArea
            value={feedback}
            onChange={handleFeedbackChange}
            placeholder={FEEDBACK_INPUT_CONSTANTS.PLACEHOLDER}
            customCSS={styles.textarea(theme)}
            maxLength={FEEDBACK_INPUT_CONSTANTS.DEFAULTS.MAX_LENGTH}
            minLength={FEEDBACK_INPUT_CONSTANTS.DEFAULTS.MIN_LENGTH}
          />
        </div>
      </div>
      <BasicButton
        type='submit'
        disabled={!canSubmit}
        variant={canSubmit ? 'primary' : 'disabled'}
        icon={
          <SendIcon
            color={canSubmit ? theme.colors.white[100] : theme.colors.gray[500]}
          />
        }
        onClick={handleSubmit}
      >
        피드백 제출
      </BasicButton>
    </div>
  );
}
