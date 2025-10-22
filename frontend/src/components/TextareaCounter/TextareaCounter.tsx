import { textareaCounter } from '@/components/TextareaCounter/TextareaCounter.styles';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import { useAppTheme } from '@/hooks/useAppTheme';

interface TextareaCounter {
  textLength: number;
}

export default function TextareaCounter({ textLength }: TextareaCounter) {
  const theme = useAppTheme();
  return (
    <p css={textareaCounter(theme)}>
      {textLength} / {FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}
    </p>
  );
}
