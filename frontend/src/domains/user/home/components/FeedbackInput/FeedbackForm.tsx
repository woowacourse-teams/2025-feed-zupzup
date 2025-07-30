import { useAppTheme } from '@/hooks/useAppTheme';

import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import {
  container,
  formContainer,
  userInfo,
  usernameInput,
  randomButton,
  topInputBorder,
  textareaContainer,
  textarea,
  toggleButtonContainer,
  toggleButtonText,
} from './FeedbackForm.styles';

import Button from '@/components/@commons/Button/Button';
import TextArea from '@/components/@commons/TextArea/TextArea';
import Input from '@/components/@commons/Input/Input'; // 추가
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';

export interface FeedbackFormProps {
  className?: string;
  feedback: string;
  username: string;
  isLocked: boolean;
  canSubmit: boolean;
  onFeedbackChange: (value: string) => void;
  onUsernameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onUsernameFocus: () => void;
  onRandomChange: () => void;
  onLockToggle: () => void;
}

export default function FeedbackForm({
  className,
  feedback,
  username,
  isLocked,
  onFeedbackChange,
  onUsernameChange,
  onUsernameFocus,
  onRandomChange,
  onLockToggle,
}: FeedbackFormProps) {
  const theme = useAppTheme();

  return (
    <div css={container} className={className}>
      <div css={formContainer}>
        <div css={userInfo(theme)}>
          <Input
            value={username}
            onChange={onUsernameChange}
            onFocus={onUsernameFocus}
            placeholder='사용자 이름을 입력하세요'
            customCSS={usernameInput(theme)}
            maxLength={20}
            minLength={1}
            disabled={isLocked}
          />
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
            placeholder={FEEDBACK_FORM_CONSTANTS.PLACEHOLDER}
            customCSS={textarea(theme)}
            maxLength={FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}
            minLength={FEEDBACK_FORM_CONSTANTS.DEFAULTS.MIN_LENGTH}
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
