import { theme } from '@/theme';
import { SuggestionsFormFieldProps } from './SuggestionsFormField.type';
import { fieldLabel, formField } from './SuggestionsFormField.style';

export default function SuggestionsFormField({
  label,
  children,
}: SuggestionsFormFieldProps) {
  return (
    <section css={formField}>
      <div css={[fieldLabel, theme.typography.inter.small]}>{label}</div>
      {children}
    </section>
  );
}
