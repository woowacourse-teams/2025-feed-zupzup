import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/api.types';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onError: () => void;
  onSuccess: () => void;
}

interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
  onError: () => void;
  onSuccess: (data: Response) => void;
}

type AdminAuthData = {
  loginId: string;
  adminName: string;
  adminId: number;
};

type AdminLoginResponse = ApiResponse<AdminAuthData>;

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

export async function postAdminSignup({
  loginId,
  password,
  adminName,
  onError,
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
      onError,
      onSuccess,
    }
  );
}
