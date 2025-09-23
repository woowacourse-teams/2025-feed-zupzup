import BasicButton from '@/components/BasicButton/BasicButton';
import useNavigation from '@/domains/hooks/useNavigation';
import { buttonContainer } from './ActionButtons.styles';

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
