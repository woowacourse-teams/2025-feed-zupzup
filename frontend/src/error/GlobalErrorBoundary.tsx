import { ApiError } from '@/apis/apiClient';
import React, { ComponentType, PropsWithChildren } from 'react';

export interface FallbackProps {
  resetErrorBoundary: () => void;
  error: Error | ApiError;
}

interface ErrorBoundaryProps {
  fallback: ComponentType<FallbackProps>;
  onReset?: () => void;
}

interface ErrorBoundaryState {
  error: Error | null;
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
    this.props.onReset?.();
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
