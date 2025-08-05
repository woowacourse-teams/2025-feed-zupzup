import { BaseEvent } from './types';

declare global {
  function gtag(
    command: 'config' | 'event' | 'js' | 'set',
    targetId?: string | Date,
    config?: Record<string, string | number | boolean>
  ): void;
}

export const Analytics = {
  track: (event: BaseEvent): void => {
    if (process.env.NODE_ENV === 'development') {
      console.log('📊 Analytics Event:', event);
    }

    try {
      gtag(
        'event',
        event.name,
        event.parameters as Record<string, string | number | boolean>
      );
    } catch (error) {
      if (process.env.NODE_ENV === 'development') {
        console.error('Analytics tracking failed:', error);
      }
    }
  },
};
