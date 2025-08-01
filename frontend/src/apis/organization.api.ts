import { apiClient } from '@/apis/apiClient';
import { GetOrganizationName } from '@/types/organization.types';

export async function getOrganizationName({
  organizationId,
}: {
  organizationId: number;
}) {
  const response = await apiClient.get<GetOrganizationName>(
    `/organizations/${organizationId}`
  );
  return response as GetOrganizationName;
}
