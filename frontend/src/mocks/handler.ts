import { AdminFeedbackHandlers } from '@/mocks/handlers/admin/feedback';
import { UserFeedbackHandlers } from '@/mocks/handlers/user/feedback';

export const handler = [...UserFeedbackHandlers, ...AdminFeedbackHandlers];
