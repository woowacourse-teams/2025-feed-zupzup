import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const offlineBannerStyle = (theme: Theme) => css`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: ${theme.colors.purple[100]};
  color: white;
  padding: 16px 24px;
  text-align: center;
  z-index: 1100;
  font: ${theme.typography.pretendard.captionSmall};
  box-shadow: 0 4px 12px rgba(115, 86, 255, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideDown 0.3s ease-out;

  @keyframes slideDown {
    from {
      transform: translateY(-100%);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }
`;
