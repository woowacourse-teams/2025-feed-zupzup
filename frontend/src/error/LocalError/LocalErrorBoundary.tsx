import { ApiError, NetworkError } from '@/apis/apiClient';
import React, { ComponentType, PropsWithChildren } from 'react';

export interface FallbackProps {
  resetErrorBoundary: () => void;
  error: ApiError | NetworkError;
  queryKey: readonly string[];
}

interface ErrorBoundaryProps {
  fallback: ComponentType<FallbackProps>;
  queryKey: readonly string[];
}

interface ErrorBoundaryState {
  error: ApiError | NetworkError | null;
}

const initialState = {
  error: null,
};

export default class LocalErrorBoundary extends React.Component<
  PropsWithChildren<ErrorBoundaryProps>,
  ErrorBoundaryState
> {
  constructor(props: PropsWithChildren<ErrorBoundaryProps>) {
    super(props);
    this.state = initialState;
  }

  static getDerivedStateFromError(error: Error) {
    if (error instanceof NetworkError) {
      return { error };
    }

    if (error instanceof ApiError) {
      if (!isKnownError(error)) {
        throw error;
      }
      return { error };
    } else {
      throw error;
    }
  }

  resetErrorBoundary = () => {
    this.setState({ ...initialState });
  };

  render() {
    const { fallback: Fallback } = this.props;

    if (this.state.error) {
      return (
        <Fallback
          resetErrorBoundary={this.resetErrorBoundary}
          error={this.state.error}
          queryKey={this.props.queryKey}
        />
      );
    }

    return this.props.children;
  }
}

function isKnownError(error: ApiError | NetworkError) {
  if (error instanceof NetworkError) {
    return true;
  }

  if (error instanceof ApiError) {
    return (
      error.status === 400 || error.status === 500 || error.status === 1000
    );
  }

  return false;
}
