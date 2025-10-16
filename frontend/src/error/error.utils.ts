import { ApiError, NetworkError } from '@/apis/apiClient';

export function getErrorName(error: Error | ApiError | NetworkError) {
  if (error instanceof NetworkError) {
    return 'NETWORK_ERROR';
  }

  if (error instanceof ApiError) {
    if (error.status === 500) {
      return 'SERVER_ERROR';
    }
    if (error.status === 400) {
      return 'FAULT_REQUEST';
    }

    if (error.status === 401 || error.status === 403) {
      return 'AUTH_ERROR';
    }
  }
  return 'UNKNOWN_ERROR';
}
