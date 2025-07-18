import { css } from '@emotion/react';
import Calendar from '../icons/Calendar';
import LikeButton from '../LikeButton/LikeButton';
import { Theme } from '@/theme';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function FeedbackBoxFooter() {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <Calendar />
        <div css={day}>2025-01-08</div>
      </div>
      <LikeButton like={false} />
    </div>
  );
}

const container = css`
  display: flex;
  justify-content: space-between;
`;

const calendar = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  font-size: 8px;
  color: ${theme.colors.gray[600]};
  vertical-align: middle;
`;

const day = css`
  line-height: 1;
`;
