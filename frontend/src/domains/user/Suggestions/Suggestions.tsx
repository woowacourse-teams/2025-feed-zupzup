import BasicTextArea from '@/components/BasicTextArea/BasicTextArea';
import Header from '@/components/Header/Header';
import { css } from '@emotion/react';
import { useState } from 'react';
import Banner from './components/Banner/Banner';
import SuggestionsFormField from './components/SuggestionsFormField/SuggestionsFormField';
import UploadBox from './components/UploadBox/UploadBox';
import SecretPostOption from './components/SecretPostOption/SecretPostOption';

export default function Suggestions() {
  const [suggestions, setSuggestions] = useState<string>('');
  const handleSuggestionsChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setSuggestions(e.target.value);
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
        <SuggestionsFormField label='카테고리 선택'>
          <div
            css={css`
              background-color: blueviolet;
            `}
          >
            카테코리 선택
          </div>
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
      </div>
    </>
  );
}

const suggestionLayout = css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: calc(100vh - 64px);
  margin-top: 64px; /* Header height */
  overflow-y: auto;
`;
