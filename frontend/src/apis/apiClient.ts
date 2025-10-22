import { logError, ErrorType, ErrorSeverity } from '@/utils/errorLogger';
import {
  DEFAULT_ERROR_MESSAGE,
  FETCH_ERROR_MESSAGE,
} from '@/constants/errorMessage';
import {
  isEmptyResponse,
  isErrorWithStatus,
  isSuccess,
} from '@/utils/fetchUtils';

type FetchMethodType = 'GET' | 'POST' | 'DELETE' | 'PATCH' | 'PUT';

interface ApiClientProps<RequestBody, Response> {
  method: FetchMethodType;
  URI: string;
  body?: RequestBody;
  onSuccess?: (data: Response) => void;
  onError?: () => void;
}

export const apiClient = {
  get: <Response>(
    URI: string,
    options?: Omit<ApiClientProps<undefined, Response>, 'method' | 'URI'>
  ) => baseClient<Response, undefined>({ ...options, method: 'GET', URI }),

  post: <Response, RequestBody>(
    URI: string,
    body: RequestBody,
    options?: Omit<
      ApiClientProps<RequestBody, Response>,
      'method' | 'URI' | 'body'
    >
  ) =>
    baseClient<Response, RequestBody>({
      ...options,
      method: 'POST',
      URI,
      body,
    }),

  put: <Response, RequestBody>(
    URI: string,
    body: RequestBody,
    options?: Omit<
      ApiClientProps<RequestBody, Response>,
      'method' | 'URI' | 'body'
    >
  ) =>
    baseClient<Response, RequestBody>({
      ...options,
      method: 'PUT',
      URI,
      body,
    }),

  delete: <Response>(
    URI: string,
    options?: Omit<ApiClientProps<undefined, Response>, 'method' | 'URI'>
  ) =>
    baseClient<Response, undefined>({
      ...options,
      method: 'DELETE',
      URI,
    }),

  deleteWithBody: <Response, RequestBody>(
    URI: string,
    body: RequestBody,
    options?: Omit<
      ApiClientProps<RequestBody, Response>,
      'method' | 'URI' | 'body'
    >
  ) =>
    baseClient<Response, RequestBody>({
      ...options,
      method: 'DELETE',
      URI,
      body,
    }),

  patch: <Response, RequestBody>(
    URI: string,
    body: RequestBody,
    options?: Omit<
      ApiClientProps<RequestBody, Response>,
      'method' | 'URI' | 'body'
    >
  ) =>
    baseClient<Response, RequestBody>({
      ...options,
      method: 'PATCH',
      URI,
      body,
    }),
};

async function baseClient<Response, RequestBody>({
  method,
  URI,
  body,
  onSuccess,
  onError,
}: ApiClientProps<RequestBody, Response>): Promise<Response | void> {
  const headers: Record<string, string> = {
    'Content-type': 'application/json',
  };

  const isLocal = process.env.ENV_MODE === 'local';
  const baseURL = isLocal ? '/api' : process.env.BASE_URL;
  const fullURL = `${baseURL}${URI}`;

  try {
    const response = await fetchWithTimeout(fullURL, {
      method,
      headers,
      body: body ? JSON.stringify(body) : null,
      credentials: 'include',
      mode: 'cors',
    });

    if (isEmptyResponse(response)) return;

    if (isSuccess(response)) {
      const data = await response.json();
      onSuccess?.(data);
      return data;
    }

    if (isErrorWithStatus(response)) {
      let message =
        FETCH_ERROR_MESSAGE[String(response.status)] ?? DEFAULT_ERROR_MESSAGE;

      try {
        const errorBody = await response.json();
        if (errorBody && typeof errorBody.message === 'string') {
          message = errorBody.message;
        }
      } catch {
        // response.json() 파싱 실패 시 기본 메시지를 유지
      }

      const apiError = new ApiError(response.status, message);

      let severity = ErrorSeverity.MEDIUM;
      if (response.status >= 500) severity = ErrorSeverity.HIGH;
      if (response.status === 401) severity = ErrorSeverity.LOW;

      logError(apiError, ErrorType.API_ERROR, severity);

      throw apiError;
    }

    throw new Error(DEFAULT_ERROR_MESSAGE);
  } catch (error) {
    onError?.();

    if (error instanceof ApiError) {
      throw error;
    }

    const networkError = new NetworkError(
      error instanceof Error && error.message
        ? error.message
        : DEFAULT_ERROR_MESSAGE
    );

    logError(networkError, ErrorType.NETWORK_ERROR, ErrorSeverity.HIGH);
    throw networkError;
  }
}

const fetchWithTimeout = (
  input: RequestInfo,
  init?: RequestInit,
  timeout = 5000
): Promise<Response> => {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      reject(new Error('요청 시간이 초과되었습니다.'));
    }, timeout);

    fetch(input, init)
      .then((res) => {
        clearTimeout(timer);
        resolve(res);
      })
      .catch((err) => {
        clearTimeout(timer);
        reject(err);
      });
  });
};

export class ApiError extends Error {
  constructor(
    public status: number,
    public message: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export class NetworkError extends Error {
  constructor(message: string = '네트워크 연결에 문제가 발생했습니다.') {
    super(message);
    this.name = 'NetworkError';
  }
}
