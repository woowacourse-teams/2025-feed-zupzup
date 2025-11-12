import { useAppTheme } from '@/hooks/useAppTheme';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import {
  container,
  formContainer,
  randomButton,
  textarea,
  textareaContainer,
  formFooterContainer,
  toggleButtonText,
  topInputBorder,
  userInfo,
  usernameInput,
  toggleButtonContainer,
} from './FeedbackForm.styles';
import Button from '@/components/@commons/Button/Button';
import Input from '@/components/@commons/Input/Input';
import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import TextareaCounter from '@/components/TextareaCounter/TextareaCounter';
import ImageUploadWithPreview from '@/domains/user/home/components/ImageUploadWithPreview/ImageUploadWithPreview';

export interface FeedbackFormProps {
  className?: string;
  feedback: string;
  username: string;
  isLocked: boolean;
  canSubmit: boolean;
  file: File | null;
  imgUrl: string | null;
  onChangeFile: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onFeedbackChange: (value: string) => void;
  onUsernameChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onUsernameFocus: () => void;
  onRandomChange: () => void;
  onLockToggle: () => void;
  handleCancelFile: () => void;
}

export default function FeedbackForm({
  className,
  feedback,
  username,
  isLocked,
  file,
  imgUrl,
  onChangeFile,
  onFeedbackChange,
  onUsernameChange,
  onUsernameFocus,
  onRandomChange,
  onLockToggle,
  handleCancelFile,
}: FeedbackFormProps) {
  const theme = useAppTheme();

  return (
    <div css={container} className={className}>
      <div css={formContainer}>
        <div css={userInfo(theme)}>
          <Input
            css={topInputBorder(theme)}
            value={username}
            onChange={onUsernameChange}
            onFocus={onUsernameFocus}
            placeholder='사용자 이름을 입력하세요'
            customCSS={usernameInput(theme)}
            maxLength={10}
            minLength={1}
          />
        </div>

        <Button
          onClick={onRandomChange}
          css={randomButton(theme)}
          type='button'
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
          <TextareaCounter
            textLength={feedback.length}
            right='16px'
            bottom='8px'
          />
        </div>
      </div>
      <div css={formFooterContainer}>
        <ImageUploadWithPreview
          file={file}
          imgUrl={imgUrl}
          onChangeFile={onChangeFile}
          onCancelFile={handleCancelFile}
        />
        <div css={toggleButtonContainer}>
          <BasicToggleButton
            isToggled={isLocked}
            onClick={onLockToggle}
            name='lock'
          />
          <p css={toggleButtonText(theme)}>비밀글로 작성</p>
        </div>
      </div>
    </div>
  );
}
