import Header from '@/components/Header/Header';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function Suggestions() {
  const theme = useAppTheme();

  return (
    <>
      <Header />
      <div css={suggestions(theme)}>
        <div>suggestion</div>
      </div>
    </>
  );
}

const suggestions = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 100vh;
  margin-top: 64px; /* Header height */
  background-color: ${theme.colors.white[300]};
`;
