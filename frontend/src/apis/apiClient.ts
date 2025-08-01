import {
  DEFAULT_ERROR_MESSAGE,
  FETCH_ERROR_MESSAGE,
} from '@/constants/errorMessage';
import {
  isEmptyResponse,
  isErrorWithStatus,
  isSuccess,
} from '@/utils/fetchUtils';

type FetchMethodType = 'GET' | 'POST' | 'DELETE' | 'PATCH';

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

  const fullURL = `${process.env.BASE_URL}${URI}`;

  try {
    const response = await fetchWithTimeout(fullURL, {
      method,
      headers,
      body: body ? JSON.stringify(body) : null,
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

      throw new ApiError(response.status, message);
    }

    throw new Error(DEFAULT_ERROR_MESSAGE);
  } catch (error) {
    onError?.();
    if (error instanceof ApiError) {
      throw error;
    }

    const message =
      error instanceof Error && error.message
        ? error.message
        : DEFAULT_ERROR_MESSAGE;

    throw new ApiError(0, message);
  }
}

const fetchWithTimeout = (
  input: RequestInfo,
  init?: RequestInit,
  timeout = 5000
): Promise<Response> => {
  // 네트워크 오류
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
