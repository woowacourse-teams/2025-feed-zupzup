import { apiClient, ApiError } from '@/apis/apiClient';
import { GetOrganizationName } from '@/types/organization.types';

interface PostOrganizationCheerParams {
  organizationId: number;
  cheeringCount: number;
}

interface GetOrganizationNameParams {
  organizationId: number;
}

export async function getOrganizationName({
  organizationId,
}: GetOrganizationNameParams) {
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

export async function postOrganizationCheer({
  organizationId,
  cheeringCount,
}: PostOrganizationCheerParams) {
  try {
    const response = await apiClient.post(
      `/organizations/${organizationId}/cheer`,
      {
        cheeringCount,
      }
    );
    return response;
  } catch (e) {
    if (e instanceof ApiError) console.error(e.message);
    return;
  }
}
