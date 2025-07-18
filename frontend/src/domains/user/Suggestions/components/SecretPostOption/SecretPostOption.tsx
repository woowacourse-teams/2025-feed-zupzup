import Lock from '@/components/icons/lock';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function SecretPostOption() {
  const theme = useAppTheme();
  return (
    <div css={secretOption(theme)}>
      <div
        css={css`
          background-color: blueviolet;
        `}
      >
        lockButton
      </div>
      <Lock />
      <p css={secretText(theme)}>비밀글로 작성</p>
    </div>
  );
}

const secretOption = (theme: Theme) => css`
  background-color: ${theme.colors.gray[100]};
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  width: 100%;
  border-radius: 16px;
`;

const secretText = (theme: Theme) => css`
  color: ${theme.colors.darkGray[200]};
  ${theme.typography.inter.small}
`;
