import { FeedbackFilterType, SortType } from '@/types/feedback.types';

interface BuildUrlParams {
  organizationId: number | string;
  sort: SortType;
  filter: FeedbackFilterType | '';
  isAdmin: boolean;
}

export function createFeedbacksUrl({
  organizationId,
  sort,
  filter,
  isAdmin,
}: BuildUrlParams) {
  const baseUrl = `${isAdmin ? '/admin' : ''}/organizations/${organizationId}/feedbacks`;
  const params = new URLSearchParams();

  params.set('orderBy', sort);
  params.set('status', filter);

  return `${baseUrl}?${params.toString()}`;
}
