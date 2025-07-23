import { postUserFeedback } from '@/apis/userFeedback.api';
import BasicButton from '@/components/BasicButton/BasicButton';
import BasicInput from '@/components/BasicInput/BasicInput';
import BasicTextArea from '@/components/BasicTextArea/BasicTextArea';
import Header from '@/components/Header/Header';
import CategorySelector from '@/domains/components/CategorySelector/CategorySelector';
import Banner from '@/domains/user/suggestions/components/Banner/Banner';
import SecretPostOption from '@/domains/user/suggestions/components/SecretPostOption/SecretPostOption';
import SuggestionsFormField from '@/domains/user/suggestions/components/SuggestionsFormField/SuggestionsFormField';
import UploadBox from '@/domains/user/suggestions/components/UploadBox/UploadBox';
import useSuggestionForm from '@/domains/user/suggestions/hooks/useSuggestionForm';
import {
  buttonContainer,
  suggestionLayout,
} from '@/domains/user/suggestions/Suggestions.style';
import { useNavigate } from 'react-router-dom';

const selectorOptions = [
  { value: 'general', label: '일반' },
  { value: 'other', label: '기타' },
];

export default function Suggestions() {
  const navigate = useNavigate();

  const { values, handleSuggestionForm } = useSuggestionForm({
    initialValues: {
      suggestions: '',
      userName: '',
      selectedCategory: '',
      isSecret: false,
      imgSrc: '',
    },
  });

  const handleSubmitSuggestions = () => {
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

  return (
    <>
      <Header title='건의사항 작성' subtitle='소중한 의견을 들려주세요' />
      <div css={suggestionLayout}>
        <Banner
          title='더 나은 서비스를 위해'
          description='여러분의 소중한 의견이 더 좋은 카페를 만들어
갑니다'
        />
        <form css={suggestionLayout}>
          <SuggestionsFormField label='카테고리 선택'>
            <CategorySelector
              name='selectedCategory'
              options={selectorOptions}
              placeholder='카테고리를 선택하세요'
              value={values.selectedCategory}
              onChange={handleSuggestionForm}
            />
          </SuggestionsFormField>

          <SuggestionsFormField label='작성자 이름'>
            <BasicInput
              name='userName'
              value={values.userName}
              onChange={handleSuggestionForm}
              placeholder='작성자 이름은 익명으로 표시됩니다.'
            />
          </SuggestionsFormField>

          <SuggestionsFormField label='내용'>
            <BasicTextArea
              name='suggestions'
              value={values.suggestions}
              onChange={handleSuggestionForm}
              placeholder='건의하고 싶은 내용을 자세히 입력해주세요'
            />
          </SuggestionsFormField>

          <SuggestionsFormField label='이미지 첨부 (선택사항)'>
            <UploadBox
              name='imgSrc'
              imgSrc={values.imgSrc}
              handleImageUpload={(url) =>
                handleSuggestionForm({ name: 'imgSrc', value: url })
              }
            />
          </SuggestionsFormField>

          <SecretPostOption
            name='isSecret'
            isSecret={values.isSecret}
            handleToggleButton={() =>
              handleSuggestionForm({
                name: 'isSecret',
                value: !values.isSecret,
              })
            }
          />

          <div css={buttonContainer}>
            <BasicButton variant='secondary' onClick={() => navigate(-1)}>
              취소
            </BasicButton>
            <BasicButton onClick={handleSubmitSuggestions}>
              등록하기
            </BasicButton>
          </div>
        </form>
      </div>
    </>
  );
}
