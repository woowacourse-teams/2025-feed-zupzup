export interface AISummaryCategory {
  clusteredId: string;
  content: string;
  totalCount: number;
}

export interface AISummaryData {
  clusterRepresentativeFeedbacks: AISummaryCategory[];
}
