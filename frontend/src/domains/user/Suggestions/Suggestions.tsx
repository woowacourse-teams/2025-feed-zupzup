import BasicTextArea from '@/components/BasicTextArea/BasicTextArea';

import Header from '@/components/Header/Header';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

import Banner from './components/Banner/Banner';
import SuggestionsFormField from './components/SuggestionsFormField/SuggestionsFormField';
import { useState } from 'react';

export default function Suggestions() {
  const theme = useAppTheme();

  const [suggestions, setSuggestions] = useState<string>('');
  const handleSuggestionsChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setSuggestions(e.target.value);
  };

  return (
    <>
      <Header title='건의사항 작성' subtitle='소중한 의견을 들려주세요' />
      <div css={suggestionLayout(theme)}>
        <Banner
          title='더 나은 서비스를 위해'
          description='여러분의 소중한 의견이 더 좋은 카페를 만들어
갑니다'
        />
        <SuggestionsFormField label='카테고리 선택'>
          <div>카테코리 선택</div>
        </SuggestionsFormField>

        <SuggestionsFormField label='내용'>
          <BasicTextArea
            onChange={handleSuggestionsChange}
            value={suggestions}
            placeholder='건의하고 싶은 내용을 자세히 입력해주세요'
          />
        </SuggestionsFormField>
      </div>
    </>
  );
}

const suggestionLayout = (theme: Theme) => css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: 100vh;
  margin-top: 64px; /* Header height */
  background-color: ${theme.colors.white[300]};
`;
