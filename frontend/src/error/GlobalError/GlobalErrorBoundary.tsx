import { ApiError, NetworkError } from '@/apis/apiClient';
import React, { ComponentType, PropsWithChildren } from 'react';

export interface FallbackProps {
  resetErrorBoundary: () => void;
  error: Error | ApiError | NetworkError;
}

interface ErrorBoundaryProps {
  fallback: ComponentType<FallbackProps>;
}

interface ErrorBoundaryState {
  error: Error | ApiError | NetworkError | null;
}

const initialState = {
  error: null,
};

export default class GlobalErrorBoundary extends React.Component<
  PropsWithChildren<ErrorBoundaryProps>,
  ErrorBoundaryState
> {
  constructor(props: PropsWithChildren<ErrorBoundaryProps>) {
    super(props);
    this.state = initialState;
  }

  static getDerivedStateFromError(error: Error) {
    return { error };
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
        />
      );
    }

    return this.props.children;
  }
}
