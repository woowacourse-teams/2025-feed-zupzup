import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

export type AdminOrganizationType = {
  uuid: string;
  name: string;
  waitingCount: number;
  postedAt: string;
};

export type GetAdminOrganizationResponse = ApiResponse<AdminOrganizationType[]>;

export async function getAdminOrganization(): Promise<GetAdminOrganizationResponse> {
  const response = await apiClient.get('/admin/organizations');

  return response as GetAdminOrganizationResponse;
}
