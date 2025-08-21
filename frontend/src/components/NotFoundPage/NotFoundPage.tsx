import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  container,
  content,
  errorCode,
  title,
  description,
} from './NotFoundPage.styles';
import useNavigation from '@/domains/hooks/useNavigation';

export default function NotFoundPage() {
  const theme = useAppTheme();
  const { goBack } = useNavigation();

  return (
    <div css={container}>
      <div css={content}>
        <div css={errorCode(theme)}>404</div>
        <h1 css={title(theme)}>페이지를 찾을 수 없어요</h1>
        <p css={description(theme)}>
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>
        <BasicButton onClick={goBack} variant='primary' height={'30px'}>
          돌아가기
        </BasicButton>
      </div>
    </div>
  );
}
