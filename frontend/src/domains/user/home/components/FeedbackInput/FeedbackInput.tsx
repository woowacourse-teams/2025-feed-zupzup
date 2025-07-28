import LockIcon from '@/components/icons/LockIcon';
import UnlockIcon from '@/components/icons/UnlockIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useFeedbackForm } from '@/domains/user/home/hooks/useFeedbackForm';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';
import * as styles from './FeedbackInput.styles';
import BasicButton from '@/components/BasicButton/BasicButton';
import SendIcon from '@/components/icons/SendIcon';

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
  } = useFeedbackForm();

  return (
    <div css={styles.container} className={className}>
      <div css={styles.formContainer}>
        <div css={styles.userInfo(theme)}>
          <p>{username}</p>
        </div>

        <button
          type='button'
          onClick={handleRandomChange}
          disabled={isLocked}
          css={styles.randomButton(theme)}
        >
          랜덤변경
        </button>

        <div
          css={styles.avatar}
          style={{ backgroundImage: `url('${currentAvatar}')` }}
        />

        <div css={styles.topInputBorder(theme)} />

        <button
          type='button'
          onClick={handleLockToggle}
          css={styles.lockButton(theme)}
        >
          {isLocked ? <LockIcon /> : <UnlockIcon />}
        </button>

        <div css={styles.textareaContainer(theme)}>
          <textarea
            value={feedback}
            onChange={handleFeedbackChange}
            placeholder={FEEDBACK_INPUT_CONSTANTS.PLACEHOLDER}
            css={styles.textarea(theme)}
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
      >
        피드백 제출
      </BasicButton>
    </div>
  );
}
