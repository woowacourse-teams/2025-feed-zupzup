export const QUERY_KEYS = {
  organizationStatistics: (id: number) => ['organizationStatistics', id],
  infiniteFeedbacks: ['infinity', 'feedbacks'],
  notificationSettings: () => ['notificationSettings'] as const,
};
