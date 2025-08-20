import { CategoryType } from '@/analytics/types';
import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

export type AdminOrganizationType = {
  uuid: string;
  name: string;
  waitingCount: number;
  postedAt: string;
};

export type GetAdminOrganizationResponse = ApiResponse<AdminOrganizationType[]>;

type RequestData = {
  organizationName: string;
  categories: CategoryType[];
};

export type AdminOrganizationUUIDType = {
  organizationUuid: string;
};

export async function getAdminOrganization(): Promise<GetAdminOrganizationResponse> {
  const response = await apiClient.get('/admin/organizations');

  return response as GetAdminOrganizationResponse;
}

export async function postAdminOrganization({
  organizationName,
  categories,
}: RequestData): Promise<AdminOrganizationUUIDType> {
  const response = await apiClient.post('/admin/organizations', {
    organizationName,
    categories,
  });

  return response as AdminOrganizationUUIDType;
}
