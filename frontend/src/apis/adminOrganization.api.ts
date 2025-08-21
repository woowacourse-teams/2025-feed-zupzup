import { CategoryListType } from './../constants/categoryList';
import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

export type AdminOrganizationType = {
  uuid: string;
  name: string;
  waitingCount: number;
  postedAt: string;
};

type GetAdminOrganizationResponse = ApiResponse<AdminOrganizationType[]>;

type RequestData = {
  organizationName: string;
  categories: CategoryListType[];
};

type AdminOrganizationUUIDType = {
  organizationUuid: string;
};

export async function getAdminOrganization() {
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
