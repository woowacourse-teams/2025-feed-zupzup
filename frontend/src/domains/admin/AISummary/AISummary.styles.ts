import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const aiSummaryTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}
  background-color: rgba(115, 86, 255, 0.1);
  padding: 10px 20px;
  border-radius: 8px;
  text-align: center;
`;
