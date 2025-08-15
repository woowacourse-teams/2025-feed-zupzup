import { apiClient } from '@/apis/apiClient';
import {
  GetOrganizationName,
  GetOrganizationStatistics,
} from '@/types/organization.types';

interface PostOrganizationCheerParams {
  organizationId: number;
  cheeringCount: number;
}

interface GetOrganizationNameParams {
  organizationId: number;
}

interface GetOrganizationStatistic {
  organizationId: number;
}

export async function getOrganizationName({
  organizationId,
}: GetOrganizationNameParams) {
  const response = await apiClient.get<GetOrganizationName>(
    `/organizations/${organizationId}`
  );
  return response as GetOrganizationName;
}

export async function postOrganizationCheer({
  organizationId,
  cheeringCount,
}: PostOrganizationCheerParams) {
  const response = await apiClient.post(
    `/organizations/${organizationId}/cheer`,
    {
      cheeringCount,
    }
  );
  return response;
}

export async function getOrganizationStatistics({
  organizationId,
}: GetOrganizationStatistic) {
  const response = await apiClient.get<GetOrganizationStatistics>(
    `/organizations/${organizationId}/statistic?`
  );
  return response as GetOrganizationStatistics;
}
