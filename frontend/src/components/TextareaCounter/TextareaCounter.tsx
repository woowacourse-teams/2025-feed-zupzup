import { textareaCounter } from '@/components/TextareaCounter/TextareaCounter.styles';
import { FEEDBACK_FORM_CONSTANTS } from '@/domains/user/home/constants/FeedbackForm';
import { useAppTheme } from '@/hooks/useAppTheme';

interface TextareaCounter {
  textLength: number;
  top?: string;
  left?: string;
  right?: string;
  bottom?: string;
}

export default function TextareaCounter({
  textLength,
  top,
  left,
  right,
  bottom,
}: TextareaCounter) {
  const theme = useAppTheme();
  const inset = `${top ?? 'auto'} ${right ?? 'auto'} ${bottom ?? 'auto'} ${
    left ?? 'auto'
  }`;
  return (
    <p css={textareaCounter(theme, inset)}>
      {textLength} / {FEEDBACK_FORM_CONSTANTS.DEFAULTS.MAX_LENGTH}
    </p>
  );
}
