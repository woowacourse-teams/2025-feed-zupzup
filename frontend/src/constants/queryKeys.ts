export const QUERY_KEYS = {
  organizationStatistics: (id: string) => ['organizationStatistics', id],
  infiniteFeedbacks: ['infinity', 'feedbacks'],
  notificationSettings: () => ['notificationSettings'] as const,
  qrImageDownload: ['qrImageDownload'],
  qrCode: (id: string) => ['qrCode', id],
  organizationData: ['organizationData'],
  adminOrganizations: () => ['adminOrganizations'] as const,
  adminFeedbackStatistics: ['adminFeedbackStatistics'] as const,
};
