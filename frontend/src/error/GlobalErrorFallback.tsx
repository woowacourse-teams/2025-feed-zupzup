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
import { ERROR_MESSAGES } from './errorMessages';

export default function GlobalErrorFallback({
  resetErrorBoundary,
  error,
}: {
  resetErrorBoundary: () => void;
  error: Error | null;
}) {
  const queryClient = useQueryClient();
  const theme = useAppTheme();

  const handleRetry = () => {
    queryClient.resetQueries();
    resetErrorBoundary();
  };

  const errorName = getErrorName(error as ApiError);
  const errorMessage = ERROR_MESSAGES[errorName];

  return (
    <div css={container}>
      <div css={content}>
        <h1 css={title(theme)}>{errorMessage.title}</h1>
        <p css={subtitle(theme)}>{errorMessage.subtitle}</p>
        <div css={buttonContainer}>
          <BasicButton
            onClick={handleRetry}
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
  if (error.status === 401 || error.status === 403) {
    return 'AUTH_ERROR';
  }
  return 'UNKNOWN_ERROR';
}
