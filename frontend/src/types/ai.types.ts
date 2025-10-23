import { FeedbackType } from './feedback.types';

export interface AISummaryCategory {
  clusterId: number;
  label: string;
  totalCount: number;
}

export interface AISummaryData {
  clusterInfos: AISummaryCategory[];
}

export interface AISummaryDetailData {
  feedbacks: FeedbackType[];
  label: string;
  totalCount: number;
}
