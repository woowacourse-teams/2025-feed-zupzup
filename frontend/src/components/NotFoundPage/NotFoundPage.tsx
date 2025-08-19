import { useNavigate } from 'react-router-dom';
import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  container,
  content,
  errorCode,
  title,
  description,
} from './NotFoundPage.styles';

export default function NotFoundPage() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div css={container}>
      <div css={content}>
        <div css={errorCode(theme)}>404</div>
        <h1 css={title(theme)}>페이지를 찾을 수 없어요</h1>
        <p css={description(theme)}>
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>
        <BasicButton onClick={handleGoBack} variant='primary' height={'30px'}>
          돌아가기
        </BasicButton>
      </div>
    </div>
  );
}
