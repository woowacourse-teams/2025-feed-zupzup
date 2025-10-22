import { textareaCounter } from '@/components/TextareaCounter/TextareaCounter.styles';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import { useAppTheme } from '@/hooks/useAppTheme';

interface TextareaCounter {
  textLength: number;
}

export default function TextareaCounter({ textLength }: TextareaCounter) {
  const theme = useAppTheme();

  const shouldAnnounce =
    FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH - textLength <= 10;

  return (
    <p css={textareaCounter(theme)} aria-live='polite'>
      <span aria-hidden='true'>
        {`${textLength} / ${FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}`}
      </span>
      {shouldAnnounce && (
        <span className='srOnly'>
          현재 입력한 글자 수는 {textLength}자입니다. 최대
          {FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}자까지 입력할 수
          있습니다.
        </span>
      )}
    </p>
  );
}
