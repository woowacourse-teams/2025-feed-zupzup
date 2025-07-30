import {
  fieldLabel,
  formField,
} from '@/domains/user/suggestions/components/SuggestionsFormField/SuggestionsFormField.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface SuggestionsFormFieldProps {
  label: string;
  children: React.ReactNode;
}

export default function SuggestionsFormField({
  label,
  children,
}: SuggestionsFormFieldProps) {
  const theme = useAppTheme();
  return (
    <section css={formField}>
      <div css={fieldLabel(theme)}>{label}</div>
      {children}
    </section>
  );
}
