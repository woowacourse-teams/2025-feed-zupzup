import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/api.types';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onError: () => void;
  onSuccess: () => void;
}

export type AdminLoginData = {
  loginId: string;
  adminName: string;
  adminId: number;
};
type AdminLoginResponse = ApiResponse<AdminLoginData>;

type PostAdminLogoutResponse = ApiResponse<string>;

export async function postAdminLogin({
  loginId,
  password,
  onError,
  onSuccess,
}: PostAdminLoginParams): Promise<AdminLoginResponse> {
  const response = await apiClient.post(
    '/admin/login',
    {
      loginId,
      password,
    },
    {
      onError,
      onSuccess,
    }
  );
  return response as AdminLoginResponse;
}

export async function postAdminLogout(): Promise<PostAdminLogoutResponse> {
  const response = await apiClient.post('/admin/logout', {});

  return response as PostAdminLogoutResponse;
}
