import { useState } from 'react';

type SuggestionsFormValues = {
  suggestions: string;
  userName: string;
  selectedCategory: string;
  isSecret: boolean;
  imgSrc: string;
};

type ManualInput = { name: string; value: string | boolean };

type InputEvent =
  | React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  | ManualInput;

interface UseSuggestionsFormProps {
  initialValues: SuggestionsFormValues;
}

export default function useSuggestionForm({
  initialValues,
}: UseSuggestionsFormProps) {
  const [values, setValues] = useState(initialValues);

  const handleSuggestionForm = (input: InputEvent) => {
    if ('target' in input) {
      const { name, value } = input.target as HTMLInputElement;
      setValues((prev) => ({ ...prev, [name]: value }));
    } else {
      const { name, value } = input;
      setValues((prev) => ({ ...prev, [name]: value }));
    }
  };

  return { values, handleSuggestionForm };
}
