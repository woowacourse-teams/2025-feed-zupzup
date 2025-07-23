import { postUserFeedback } from '@/apis/userFeedback.api';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

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
  const navigate = useNavigate();
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

  const handleSubmitSuggestions = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    event.preventDefault();

    postUserFeedback({
      placeId: 1,
      content: values.suggestions,
      imageUrl: values.imgSrc,
      isSecret: values.isSecret,
      userName: values.userName,
    });
    alert('건의사항이 제출되었습니다!');
    navigate('/');
  };

  return { values, handleSuggestionForm, handleSubmitSuggestions };
}
