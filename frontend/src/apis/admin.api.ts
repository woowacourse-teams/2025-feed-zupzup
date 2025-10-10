import { apiClient } from '@/apis/apiClient';
import { AdminAuthData } from '@/types/adminAuth';
import { AISummaryData } from '@/types/ai.types';
import { ApiResponse } from '@/types/apiResponse';

export interface PostAdminLoginParams {
  loginId: string;
  password: string;
}

export interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
}

export interface AISummaryParams {
  organizationId: string;
}

type PostAdminLogoutResponse = ApiResponse<string>;
export type AdminAuthResponse = ApiResponse<AdminAuthData>;
export type AISummaryResponse = ApiResponse<AISummaryData>;

export async function postAdminLogin({
  loginId,
  password,
}: PostAdminLoginParams) {
  return await apiClient.post<AdminAuthResponse, PostAdminLoginParams>(
    '/admin/login',
    {
      loginId,
      password,
    }
  );
}

export async function postAdminLogout() {
  const response = await apiClient.post<PostAdminLogoutResponse, object>(
    '/admin/logout',
    {}
  );
  return response as PostAdminLogoutResponse;
}

export async function postAdminSignup({
  loginId,
  password,
  adminName,
}: PostAdminSignupParams) {
  const response = await apiClient.post<
    AdminAuthResponse,
    PostAdminSignupParams
  >('/admin/sign-up', {
    loginId,
    password,
    adminName,
  });
  return response as AdminAuthResponse;
}

export async function getAdminAuth() {
  const response = await apiClient.get<AdminAuthResponse>('/admin/me');
  return response as AdminAuthResponse;
}

export async function getAISummary({ organizationId }: AISummaryParams) {
  const response = await apiClient.get<AISummaryResponse>(
    `/admin/organizations/${organizationId}/clusters`
  );
  return response as AISummaryResponse;
}
