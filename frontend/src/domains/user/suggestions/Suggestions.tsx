import BasicButton from '@/components/BasicButton/BasicButton';
import BasicTextArea from '@/components/BasicTextArea/BasicTextArea';
import Header from '@/components/Header/Header';
import CategorySelector from '@/domains/components/CategorySelector/CategorySelector';
import Banner from '@/domains/user/suggestions/components/Banner/Banner';
import SecretPostOption from '@/domains/user/suggestions/components/SecretPostOption/SecretPostOption';
import SuggestionsFormField from '@/domains/user/suggestions/components/SuggestionsFormField/SuggestionsFormField';
import UploadBox from '@/domains/user/suggestions/components/UploadBox/UploadBox';
import {
  buttonContainer,
  suggestionLayout,
} from '@/domains/user/suggestions/Suggestions.style';
import { useState } from 'react';

const selectorOptions = [
  { value: 'general', label: '일반' },
  { value: 'other', label: '기타' },
];

export default function Suggestions() {
  const [suggestions, setSuggestions] = useState<string>('');
  const handleSuggestionsChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setSuggestions(e.target.value);
  };

  const [selectedCategory, setSelectedCategory] = useState<string>('');

  return (
    <>
      <Header title='건의사항 작성' subtitle='소중한 의견을 들려주세요' />
      <div css={suggestionLayout}>
        <Banner
          title='더 나은 서비스를 위해'
          description='여러분의 소중한 의견이 더 좋은 카페를 만들어
갑니다'
        />
        <SuggestionsFormField label='카테고리 선택'>
          <CategorySelector
            options={selectorOptions}
            placeholder='카테고리를 선택하세요'
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          />
        </SuggestionsFormField>

        <SuggestionsFormField label='내용'>
          <BasicTextArea
            onChange={handleSuggestionsChange}
            value={suggestions}
            placeholder='건의하고 싶은 내용을 자세히 입력해주세요'
          />
        </SuggestionsFormField>

        <SuggestionsFormField label='이미지 첨부 (선택사항)'>
          <UploadBox />
        </SuggestionsFormField>

        <SecretPostOption />

        <div css={buttonContainer}>
          <BasicButton variant='secondary'>취소</BasicButton>
          <BasicButton>등록하기</BasicButton>
        </div>
      </div>
    </>
  );
}
