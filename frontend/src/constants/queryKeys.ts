export const QUERY_KEYS = {
  organizationStatistics: (id: string) => ['organizationStatistics', id],
  infiniteFeedbacks: ['infinity', 'feedbacks'],
  notificationSettings: () => ['notificationSettings'] as const,
  adminOrganizations: () => ['adminOrganizations'] as const,
};
