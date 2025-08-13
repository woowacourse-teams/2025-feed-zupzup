import { setupWorker } from 'msw/browser';
import { handler } from '@/mocks/handler';

export const worker = setupWorker(...handler);
