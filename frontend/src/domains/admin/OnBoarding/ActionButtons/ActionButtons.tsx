import BasicButton from '@/components/BasicButton/BasicButton';
import { css } from '@emotion/react';

export default function ActionButtons() {
  return (
    <div css={buttonContainer}>
      <BasicButton variant='primary'>회원가입</BasicButton>
      <BasicButton variant='secondary'>로그인</BasicButton>
    </div>
  );
}

export const buttonContainer = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;
