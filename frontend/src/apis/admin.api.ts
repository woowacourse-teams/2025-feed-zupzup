import { apiClient } from '@/apis/apiClient';
import { AdminAuthData } from '@/types/adminAuth';
import { ApiResponse } from '@/types/apiResponse';

export interface PostAdminLoginParams {
  loginId: string;
  password: string;
}

interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
  onSuccess: (response: AdminAuthResponse) => void;
}

type PostAdminLogoutResponse = ApiResponse<string>;
export type AdminAuthResponse = ApiResponse<AdminAuthData>;

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

export async function postAdminLogout(): Promise<PostAdminLogoutResponse> {
  const response = await apiClient.post('/admin/logout', {});

  return response as PostAdminLogoutResponse;
}

export async function postAdminSignup({
  loginId,
  password,
  adminName,
  onSuccess,
}: PostAdminSignupParams) {
  await apiClient.post(
    '/admin/sign-up',
    {
      loginId,
      password,
      adminName,
    },
    {
      onSuccess,
    }
  );
}

export async function getAdminAuth() {
  const res = await apiClient.get<AdminAuthResponse>('/admin/me');
  return res as AdminAuthResponse;
}
