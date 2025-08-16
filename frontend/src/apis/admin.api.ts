import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/api.types';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onError: () => void;
  onSuccess: (data: Response) => void;
}

interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
  onError: () => void;
  onSuccess: (data: Response) => void;
}

export type AdminAuthData = {
  loginId: string;
  adminName: string;
  adminId: number;
};

type PostAdminLogoutResponse = ApiResponse<string>;
type GetAdminAuthResponse = ApiResponse<AdminAuthData>;

export async function postAdminLogin({
  loginId,
  password,
  onError,
  onSuccess,
}: PostAdminLoginParams) {
  await apiClient.post(
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

export async function getAdminAuth(): Promise<GetAdminAuthResponse> {
  const response = await apiClient.get('/admin/me');
  return response as GetAdminAuthResponse;
}
