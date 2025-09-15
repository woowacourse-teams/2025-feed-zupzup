import BasicButton from '@/components/BasicButton/BasicButton';
import useNavigation from '@/domains/hooks/useNavigation';
import { css } from '@emotion/react';

export default function ActionButtons() {
  const { goPath } = useNavigation();
  return (
    <div css={buttonContainer}>
      <BasicButton variant='primary' onClick={() => goPath('/signup')}>
        회원가입
      </BasicButton>
      <BasicButton variant='secondary' onClick={() => goPath('/login')}>
        로그인
      </BasicButton>
    </div>
  );
}

export const buttonContainer = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;
