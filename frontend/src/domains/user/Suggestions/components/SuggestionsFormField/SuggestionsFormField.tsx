import { theme } from '@/theme';
import { fieldLabel, formField } from './SuggestionsFormField.style';

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
