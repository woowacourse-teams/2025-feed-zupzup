import { useQueryClient } from '@tanstack/react-query';
import { useAppTheme } from '@/hooks/useAppTheme';
import BasicButton from '@/components/BasicButton/BasicButton';
import {
  content,
  title,
  subtitle,
  buttonContainer,
  reportSection,
  reportText,
  reportLink,
  container,
} from './GlobalErrorFallback.styles';
import { ApiError } from '@/apis/apiClient';
import { resetLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';
import useNavigation from '@/domains/hooks/useNavigation';

export default function GlobalErrorFallback({
  resetErrorBoundary,
  error,
}: {
  resetErrorBoundary: () => void;
  error: Error | null;
}) {
  const queryClient = useQueryClient();
  const theme = useAppTheme();
  const { goPath } = useNavigation();

  const handleRetry = () => {
    queryClient.resetQueries();
    resetErrorBoundary();
  };

  const handleLogin = () => {
    resetLocalStorage('auth');
    NotificationService.removeToken();
    goPath('/login');
    resetErrorBoundary();
  };

  const ERROR_MESSAGES = {
    UNKNOWN_ERROR: {
      title: '알 수 없는 오류',
      subtitle: '알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
    AUTH_ERROR: {
      title: '인증 오류',
      subtitle: '인증이 필요합니다. 로그인 후 다시 시도해주세요.',
      button: '로그인하기',
      onClick: handleLogin,
    },
    NETWORK_ERROR: {
      title: '네트워크 오류',
      subtitle: '네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
  } as const;

  const errorName = getErrorName(error as ApiError);
  const errorMessage = ERROR_MESSAGES[errorName];

  return (
    <div css={container}>
      <div css={content}>
        <h1 css={title(theme)}>{errorMessage.title}</h1>
        <p css={subtitle(theme)}>{errorMessage.subtitle}</p>
        <div css={buttonContainer}>
          <BasicButton
            onClick={errorMessage.onClick}
            variant='primary'
            width='120px'
            height='25px'
          >
            {errorMessage.button}
          </BasicButton>
        </div>
        <div css={reportSection}>
          <p css={reportText(theme)}>
            문제가 계속 발생하나요?
            <br />
            <a
              href='https://feedzupzup.com/6d72e77c-3ac9-4ba9-8ae3-ea5a6363d525/submit'
              target='_blank'
              rel='noopener noreferrer'
              css={reportLink(theme)}
            >
              여기로 제보해주세요
            </a>
          </p>
        </div>
      </div>
    </div>
  );
}

function getErrorName(error: ApiError) {
  console.log(error.status, error.message);
  if (error.status === 401 || error.status === 403) {
    return 'AUTH_ERROR';
  }
  if (error.status === 1000) {
    return 'NETWORK_ERROR';
  }
  return 'UNKNOWN_ERROR';
}
