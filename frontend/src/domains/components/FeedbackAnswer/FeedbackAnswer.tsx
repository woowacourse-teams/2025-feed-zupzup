import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

interface FeedbackAnswerProps {
  answer: string;
}

export default function FeedbackAnswer({ answer }: FeedbackAnswerProps) {
  const theme = useAppTheme();
  return (
    <div css={container(theme)}>
      <p css={title(theme)}>관리자 답변</p>
      <p>{answer}</p>
    </div>
  );
}

const container = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  padding: 10px;
  background-color: #fbfffd;
  border-left: 2px solid ${theme.colors.green[300]};
`;

const title = (theme: Theme) => css`
  font-weight: bold;
  color: ${theme.colors.green[300]};
`;
