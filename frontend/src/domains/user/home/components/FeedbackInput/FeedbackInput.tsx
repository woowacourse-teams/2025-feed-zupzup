import { useAppTheme } from '@/hooks/useAppTheme';

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
  toggleButtonText,
} from './FeedbackInput.styles';

import Button from '@/components/@commons/Button/Button';
import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';

export interface FeedbackInputProps {
  className?: string;
  feedback: string;
  username: string;
  isLocked: boolean;
  canSubmit: boolean;
  onFeedbackChange: (value: string) => void;
  onRandomChange: () => void;
  onLockToggle: () => void;
  onSubmit: () => void;
}

export default function FeedbackInput({
  className,
  feedback,
  username,
  isLocked,
  onFeedbackChange,
  onRandomChange,
  onLockToggle,
}: FeedbackInputProps) {
  const theme = useAppTheme();

  return (
    <div css={container} className={className}>
      <div css={formContainer}>
        <div css={userInfo(theme)}>
          <p>{username}</p>
        </div>

        <Button
          onClick={onRandomChange}
          disabled={isLocked}
          css={randomButton(theme)}
        >
          랜덤변경
        </Button>

        <div css={topInputBorder(theme)} />

        <div css={textareaContainer(theme)}>
          <TextArea
            value={feedback}
            onChange={(e) => onFeedbackChange(e.target.value)}
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
          onClick={onLockToggle}
          name='lock'
        />
        <p css={toggleButtonText(theme)}>비밀글로 작성</p>
      </div>
    </div>
  );
}
