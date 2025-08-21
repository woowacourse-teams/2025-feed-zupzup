import * as Sentry from '@sentry/react';
import { versionInfo } from '@/utils/version';

export const initSentry = () => {
  Sentry.init({
    dsn: 'https://d078b25843b4ef88ed75b287a7ab8a4b@o4509750841245696.ingest.us.sentry.io/4509750843342849',
    integrations: [
      Sentry.browserTracingIntegration(),
      Sentry.replayIntegration({
        maskAllText: false,
        maskAllInputs: true,
      }),
    ],
    tracesSampleRate: 0.2,
    replaysSessionSampleRate: 0.1,
    replaysOnErrorSampleRate: 1.0,
    release: versionInfo.version,
    environment: process.env.NODE_ENV || 'development',
    beforeSend(event) {
      if (process.env.NODE_ENV === 'development') {
        console.log('개발환경 에러:', event);
        return null;
      }
      return event;
    },
    sendDefaultPii: true,
    initialScope: {
      tags: {
        buildHash: versionInfo.buildHash,
        branch: versionInfo.branch,
      },
    },
  });

  window.Sentry = Sentry;
};
