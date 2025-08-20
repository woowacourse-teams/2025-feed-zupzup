export const QUERY_KEYS = {
  organizationStatistics: (id: string) => ['organizationStatistics', id],
  infiniteFeedbacks: ['infinity', 'feedbacks'],
  notificationSettings: () => ['notificationSettings'] as const,
  organizationData: ['organizationData'],
  adminOrganizations: () => ['adminOrganizations'] as const,
};
