import { AdminFeedbackHandlers } from '@/mocks/handlers/admin/feedback';
import { OrganizationHandler } from '@/mocks/handlers/organization/organizations';
import { UserFeedbackHandlers } from '@/mocks/handlers/user/feedback';
import { AdminNotificationHandlers } from './handlers/admin/notifications';
import { AdminHandler } from '@/mocks/handlers/login/login';

export const handler = [
  ...UserFeedbackHandlers,
  ...AdminFeedbackHandlers,
  ...AdminNotificationHandlers,
  ...OrganizationHandler,
  ...AdminHandler,
];
