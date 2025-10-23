import { useErrorContext } from '@/contexts/useErrorContext';
import {
  MutationCache,
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { useState } from 'react';

interface QueryClientBoundaryProps {
  children: React.ReactNode;
}

export default function QueryClientBoundary({
  children,
}: QueryClientBoundaryProps) {
  const { updateAppError } = useErrorContext();

  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          mutations: {
            networkMode: 'always',
          },
          queries: {
            throwOnError: true,
          },
        },
        mutationCache: new MutationCache({
          onError: (error: Error) => {
            updateAppError(error);
          },
        }),
      })
  );

  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}
