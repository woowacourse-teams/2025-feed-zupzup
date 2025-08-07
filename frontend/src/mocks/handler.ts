import { AdminFeedbackHandlers } from '@/mocks/handlers/admin/feedback';
import { OrganizationHandler } from '@/mocks/handlers/organization/organizations';
import { UserFeedbackHandlers } from '@/mocks/handlers/user/feedback';

export const handler = [
  ...UserFeedbackHandlers,
  ...AdminFeedbackHandlers,
  ...OrganizationHandler,
];
