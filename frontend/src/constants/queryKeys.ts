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
  myFeedbacks: (organizationId: string) =>
    ['myFeedbacks', organizationId] as const,
  infiniteList: (key: string, url: string, size: number) =>
    ['infinity', key, url, size] as const,
  aiSummary: (organizationId: string) => ['aiSummary', organizationId] as const,
  aiSummaryDetail: (organizationId: string, clusterId: string) =>
    ['aiSummaryDetail', organizationId, clusterId] as const,
  myLikeFeedbackIds: (organizationId: string) =>
    ['myLikeFeedbackIds', organizationId] as const,
};
