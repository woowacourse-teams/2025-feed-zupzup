import {
  fieldLabel,
  formField,
} from '@/domains/user/suggestions/components/SuggestionsFormField/SuggestionsFormField.style';
import { theme } from '@/theme';

export interface SuggestionsFormFieldProps {
  label: string;
  children: React.ReactNode;
}

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
