import { apiClient, ApiError } from '@/apis/apiClient';
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
  period: 'WEEK' | 'MONTH';
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

export async function getOrganizationStatistics({
  organizationId,
  period,
}: GetOrganizationStatistic) {
  try {
    const response = await apiClient.get<GetOrganizationStatistics>(
      `/organizations/${organizationId}/statistic?period=${period}`
    );
    return response as GetOrganizationStatistics;
  } catch (e) {
    if (e instanceof ApiError) console.error(e.message);
    return { data: '에러입니다.' };
  }
}
