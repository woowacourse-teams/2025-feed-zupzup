import * as Sentry from '@sentry/react';

export enum ErrorType {
  API_ERROR = 'api_error',
  UI_ERROR = 'ui_error',
  NETWORK_ERROR = 'network_error',
  BUSINESS_ERROR = 'business_error',
}

export enum ErrorSeverity {
  LOW = 'info',
  MEDIUM = 'warning',
  HIGH = 'error',
  CRITICAL = 'fatal',
}

export const logError = (
  error: Error,
  type: ErrorType,
  severity: ErrorSeverity = ErrorSeverity.MEDIUM
) => {
  Sentry.withScope((scope) => {
    scope.setTag('errorType', type);
    scope.setTag('severity', severity);
    scope.setLevel(severity);

    Sentry.captureException(error);
  });
};
