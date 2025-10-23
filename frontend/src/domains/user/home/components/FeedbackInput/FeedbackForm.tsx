import Button from '@/components/@commons/Button/Button';
import Input from '@/components/@commons/Input/Input';
import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import TextareaCounter from '@/components/TextareaCounter/TextareaCounter';
import { CategoryListType } from '@/constants/categoryList';
import ImageUploadWithPreview from '@/domains/user/home/components/ImageUploadWithPreview/ImageUploadWithPreview';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useEffect, useState } from 'react';
import {
  container,
  formContainer,
  formFooterContainer,
  randomButton,
  textarea,
  textareaContainer,
  toggleButtonContainer,
  toggleButtonText,
  topInputBorder,
  userInfo,
  usernameInput,
} from './FeedbackForm.styles';

export interface FeedbackFormProps {
  className?: string;
  feedback: string;
  username: string;
  isLocked: boolean;
  canSubmit: boolean;
  file: File | null;
  imgUrl: string | null;
  feedbackCategory: CategoryListType;
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
  feedbackCategory,
  onChangeFile,
  onFeedbackChange,
  onUsernameChange,
  onUsernameFocus,
  onRandomChange,
  onLockToggle,
  handleCancelFile,
}: FeedbackFormProps) {
  const theme = useAppTheme();

  const [isInitialLoad, setIsInitialLoad] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsInitialLoad(false);
    }, 5000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div css={container} className={className}>
      <div css={formContainer}>
        <div css={userInfo(theme)}>
          <label htmlFor='username' className='srOnly'>
            사용자 이름
          </label>
          <Input
            id='username'
            css={topInputBorder(theme)}
            value={username}
            onChange={onUsernameChange}
            onFocus={onUsernameFocus}
            placeholder='사용자 이름을 입력하세요'
            customCSS={usernameInput(theme)}
            maxLength={10}
            minLength={1}
            aria-live='polite'
            aria-label={`현재 닉네임은 ${username || '익명'} 입니다`}
          />
        </div>

        <Button
          onClick={onRandomChange}
          css={randomButton(theme)}
          type='button'
          aria-label='사용자 이름을 랜덤으로 변경'
        >
          랜덤변경
        </Button>

        <div css={topInputBorder(theme)} />

        <div css={textareaContainer(theme)}>
          <TextArea
            id='feedbackText'
            aria-labelledby='feedbackLabel'
            value={feedback}
            onChange={(e) => onFeedbackChange(e.target.value)}
            placeholder={FEEDBACK_FORM_CONSTANTS.PLACEHOLDER(feedbackCategory)}
            customCSS={textarea(theme)}
            maxLength={FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}
            minLength={FEEDBACK_FORM_CONSTANTS.DEFAULTS.MIN_LENGTH}
          />
          <TextareaCounter textLength={feedback.length} />
        </div>

        <p
          id='nicknameStatus'
          aria-live='polite'
          className='srOnly'
          aria-hidden={!isInitialLoad}
        >
          닉네임을 변경하거나 편집하시려면 상위 입력란을 이용하세요.
        </p>
      </div>
      <div css={formFooterContainer}>
        <ImageUploadWithPreview
          file={file}
          imgUrl={imgUrl}
          onChangeFile={onChangeFile}
          onCancelFile={handleCancelFile}
          aria-label='이미지 업로드 또는 미리보기'
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
