export const QUERY_KEYS = {
  organizationStatistics: (id: string) => ['organizationStatistics', id],
  infiniteFeedbacks: ['infinity', 'feedbacks'],
  notificationSettings: () => ['notificationSettings'] as const,
  qrImageDownload: ['qrImageDownload'],
  qrCode: (id: string) => ['qrCode', id],
  organizationData: ['organizationData'],
  adminOrganizations: (name: string) => ['adminOrganizations', name] as const,
  adminFeedbackStatistics: (name: string) =>
    ['adminFeedbackStatistics', name] as const,
  adminAuth: ['adminAuth'] as const,
  myFeedbacks: (
    organizationId: string,
    feedbackIds: number[],
    selectedSort: string
  ) => ['myFeedbacks', organizationId, feedbackIds, selectedSort] as const,
};
