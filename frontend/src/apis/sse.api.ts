import { apiClient } from '@/apis/apiClient';

interface GetAdminSSEParams {
  organizationId: string;
}

export async function getAdminSSE({ organizationId }: GetAdminSSEParams) {
  const response = await apiClient.get(
    `/admin/sse/subscribe/${organizationId}`
  );
  return response;
}
