import { apiClient, ApiError } from '@/apis/apiClient';
import { GetOrganizationName } from '@/types/organization.types';

export async function getOrganizationName({
  organizationId,
}: {
  organizationId: number;
}) {
  try {
    const response = await apiClient.get<GetOrganizationName>(
      `/organizations/${organizationId}`
    );
    return response as GetOrganizationName;
  } catch (e) {
    if (e instanceof ApiError) console.error(e.message);
    return;
  }
}
