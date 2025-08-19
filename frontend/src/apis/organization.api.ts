import { apiClient } from '@/apis/apiClient';
import { CategoryListType } from '@/constants/categoryList';
import { ApiResponse } from '@/types/apiResponse';
import {
  GetOrganizationName,
  GetOrganizationStatistics,
  PutOrganizationsBody,
  PutOrganizationsResponse,
} from '@/types/organization.types';

interface PostOrganizationCheerParams {
  organizationId: string;
  cheeringCount: number;
}

interface GetOrganizationNameParams {
  organizationId: string;
}

interface GetOrganizationStatistic {
  organizationId: string;
}

interface PutOrganizationsParams {
  organizationId: string;
  organizationName: string;
  categories: CategoryListType[];
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

export async function putOrganizations({
  organizationId,
  organizationName,
  categories,
}: PutOrganizationsParams) {
  const response = await apiClient.put<
    ApiResponse<PutOrganizationsResponse>,
    PutOrganizationsBody
  >(`/admin/organizations/${organizationId}?`, {
    organizationName,
    categories,
  });

  return response;
}
